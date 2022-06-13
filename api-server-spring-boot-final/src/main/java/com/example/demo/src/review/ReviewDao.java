package com.example.demo.src.review;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.review.model.*;
import com.example.demo.src.review.upload.UploadFile;
import com.example.demo.src.visit.model.GetUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReviewDao {
    @Autowired private JdbcTemplate jdbcTemplate;


    public int checkReviewAndUserId(int reviewId, Integer userId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, userId);
    }

    public int checkReviewId(int reviewId,  int userId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, userId);
    }

    public GetReviewRes getReviewDetail(int reviewId) {
        String getReviewDetailQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name,  U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.id = ? and R.status = 'ACTIVE'";

        GetReviewRes getReviewRes = jdbcTemplate.queryForObject(getReviewDetailQuery,
                (rs, rowNum) -> new GetReviewRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), reviewId );

        getReviewRes.setImgUrls(getReviewImgURLs(reviewId));
        getReviewRes.setComments(getComments(reviewId));
        getReviewRes.setReviewCnt(getReviewCnt(getReviewRes.getUserId()));
        getReviewRes.setFollowCnt(getFollowCnt(getReviewRes.getUserId()));

        return getReviewRes;

    }

    private int getFollowCnt(int userId) {
        String getFollowCntQuery = "select count(*) from follows where user_id = ? and status = 'ACTIVE' ";
        return jdbcTemplate.queryForObject(getFollowCntQuery, int.class, userId);
    }

    private int getReviewCnt(int userId) {
        String getReviewCntQuery = "select count(*) from reviews where user_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getReviewCntQuery, int.class, userId);
    }

    public List<String> getReviewImgURLs(int reviewId) {
        String getReviewImgQuery = "select img_url from images_review where review_id = ? and status = 'ACTIVE'";
        //List<String> imgUrls = new ArrayList<>();
        try {
             //jdbcTemplate.query(getReviewImgQuery,
                   // (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), reviewId);
             List<String> imgUrls =  jdbcTemplate.query(getReviewImgQuery,
                     (rs, rowNum) -> rs.getString("img_url"), reviewId);
//
//             if(imgUrls.isEmpty()) {
//                 return null;
//             }
             return imgUrls;
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }


    public List<GetCommentRes> getComments(int reviewId) {
        String getComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.is_holic, date_format(C.updated_at, '%Y-%m-%d'), U.profile_img_url " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id and C.review_id = ? " +
                "where level = 1 and C.status = 'ACTIVE' " +
                "order by `order` asc";

        List<GetCommentRes> getCommentRes = jdbcTemplate.query(getComments,
                (rs, rowNum) -> new GetCommentRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getString(7),
                        rs.getString(8)
                ), reviewId);

        for(GetCommentRes comment : getCommentRes) {
            List<GetSubComment> subComments = getSubComments(reviewId);
            for (GetSubComment getSubComment : subComments) {
                getSubComment.setParentCommentUserName(getParentCommentUserName(getSubComment.getId()));
            }
            comment.setSubComments(subComments);
        }
        return getCommentRes;
    }

    public String getParentCommentUserName(int commentId) {
        String getParentCommentUserQuery = "select user_name " +
                "from users where id = (select parent_user_id from review_comments where id = ? and status = 'ACTIVE')";

        return jdbcTemplate.queryForObject(getParentCommentUserQuery, String.class, commentId);
    }

    private List<GetSubComment> getSubComments(int groupNum) {
        String getSubComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.profile_img_url, U.is_holic, C.updated_at " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id " +
                "where level > 1 and group_num = ? and C.status ='ACTIVE' " +
                "order by `order` asc, level asc ";

        return jdbcTemplate.query(getSubComments,
                (rs, rowNum) -> new GetSubComment(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8)
                ), groupNum);
    }

    public int checkRestaurantId(int restaurantId) {
        String checkReviewQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, restaurantId);
    }

    public int createReview(int restaurantId, int userId, Review review) {
        String createReviewQuery = "insert into reviews(content, score, status, user_id, restaurant_id) " +
                "values(?, ?, 'ACTIVE', ?, ?) ";
        Object[] queryParams = new Object[]{review.getContent(), review.getScore(), userId, restaurantId};
        jdbcTemplate.update(createReviewQuery, queryParams);

        String lastInserIdQuery = "select id from reviews order by id desc limit 1";
        int reviewId =  this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);

        if(review.getFile()!=null) {
            storeReviewImg(reviewId, review.getFile());
        }
        return reviewId;
    }

    private void storeReviewImg(int reviewId, List<UploadFile> files) {
        String storeReviewImgQuery = "insert into images_review(review_id, img_url, status) " +
                "values(?, ?, 'ACTIVE')";
        for(UploadFile uploadFile : files) {
            jdbcTemplate.update(storeReviewImgQuery, reviewId, uploadFile.getStoreFileUrl());
        }
    }

    public int updateReview(Integer reviewId, Review review, int userId) {
        String updateReviewQuery = "update reviews set content = ?, score = ? where id = ? and user_id = ?";
        Object[] updateQueryParams = new Object[]{review.getContent(), review.getScore(), reviewId, userId};

        int result = jdbcTemplate.update(updateReviewQuery, updateQueryParams);

        if(review.getFile()!= null) {
            storeReviewImg(reviewId, review.getFile());
        }else {
            deleteReviewImg(reviewId);
        }

        return result;
    }

    public int deleteReview(Integer reviewId) {
        String deleteReviewQuery = "update reviews set status = 'INACTIVE' where id = ? ";
        int result = jdbcTemplate.update(deleteReviewQuery, reviewId);

        deleteReviewImg(reviewId);

        return result;
    }

    private void deleteReviewImg(Integer reviewId) {
        String deleteReviewImgQuery = "update images_review set status = 'INACTIVE' where review_id = ?";
        jdbcTemplate.update(deleteReviewImgQuery, reviewId);
    }

    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public List<GetReviewByUserRes> getReviewByUser(Integer userId, List<Integer> foodCategories, String sortOption, Double latitude, Double longitude, List<Integer> scores, Integer userIdxByJwt) {
        String getReviewByUserQuery = "select R.id, R.user_id, U.user_name, R.content, R.score,  " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "left join likes as L " +
                "on R.id = L.review_id " +
                "left join review_comments as RC " +
                "on R.id = RC.review_id " +
                "left join (SELECT * FROM (" +
                " SELECT ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n" +
                " FROM restaurants) DATA) as D " +
                "on RT.id = D.id "+
                "where R.user_id = ? and R.status = 'ACTIVE' and RT.food_category in(";

        Object[] params = new Object[foodCategories.size()+4+scores.size()];
        params[0] = latitude;
        params[1] = longitude;
        params[2] = latitude;
        params[3] = userId;

        for(int i=0; i<foodCategories.size(); i++) {
            params[i+4] = foodCategories.get(i);
            getReviewByUserQuery+="?,";
        }

        getReviewByUserQuery = getReviewByUserQuery.substring(0, getReviewByUserQuery.length()-1);
        getReviewByUserQuery += ") and R.score in(";

        for(int i=0; i<scores.size(); i++) {
            params[i+4+foodCategories.size()]=scores.get(i);
            getReviewByUserQuery+="?,";
        }

        getReviewByUserQuery = getReviewByUserQuery.substring(0, getReviewByUserQuery.length()-1);

        if(sortOption.equals("distance")) {
            getReviewByUserQuery+=") order by D."+sortOption;
        }else {
            getReviewByUserQuery+=") order by R."+sortOption+ " desc";
        }

        List<GetReviewByUserRes> getReviewRes = jdbcTemplate.query(getReviewByUserQuery,
                (rs, rowNum) -> new GetReviewByUserRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), params);

        for(GetReviewByUserRes review: getReviewRes) {
            if(review.getId() ==0){
                return new ArrayList<GetReviewByUserRes>();
            }
            review.setLikeCnt(getLikeCnt(review.getId()));
            review.setCommentCnt(getCommentCnt(review.getId()));
            review.setImgUrls(getReviewImgURLs(review.getId()));
            review.setFollowCnt(getFollowCnt(userId));
            review.setReviewCnt(getReviewCnt(userId));
            review.setIsLike(getIsLike(review.getId(), userIdxByJwt));
            review.setIsWish(getIsWish(review.getId(), userIdxByJwt));
        }
        return getReviewRes;
    }

    private Integer getCommentCnt(Integer id) {
        String getCommentCnt = "select count(*) from review_comments where review_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getCommentCnt, int.class, id);
    }

    private Integer getLikeCnt(Integer id) {
        String getLikeCnt = "select count(*) from likes where review_id = ? and status ='ACTIVE'";
        return jdbcTemplate.queryForObject(getLikeCnt, int.class, id);
    }

    private Integer getIsLike(Integer reviewId, Integer userIdxByJwt) {
        String getIsLikeQuery = "select exists (select * from likes where review_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getIsLikeQuery, int.class , reviewId, userIdxByJwt);
    }

    public List<GetReviewByUserRes> getReviewByUser(Integer userId,  List<Integer> foodCategories, String sortOption, List<Integer> scores, Integer userIdxByJwt) {
        String getReviewByUserQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "left join likes as L " +
                "on R.id = L.review_id " +
                "left join review_comments as RC " +
                "on R.id = RC.review_id " +
                "where R.user_id = ? and R.status = 'ACTIVE' and RT.food_category in(";

        Object[] params = new Object[foodCategories.size()+1+scores.size()];
        params[0] = userId;
        for(int i=0; i<foodCategories.size(); i++) {
            params[i+1] = foodCategories.get(i);
            getReviewByUserQuery+="?,";
        }

        getReviewByUserQuery = getReviewByUserQuery.substring(0, getReviewByUserQuery.length()-1);
        getReviewByUserQuery += ") and R.score in(";

        for(int i=0; i<scores.size(); i++) {
            params[i+1+foodCategories.size()]=scores.get(i);
            getReviewByUserQuery+="?,";
        }

        getReviewByUserQuery = getReviewByUserQuery.substring(0, getReviewByUserQuery.length()-1);
        getReviewByUserQuery+=") order by R."+sortOption+" desc";



        List<GetReviewByUserRes> getReviewRes = jdbcTemplate.query(getReviewByUserQuery,
                (rs, rowNum) -> new GetReviewByUserRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getBoolean(9),
                        rs.getString(10)
                ), params);

        for(GetReviewByUserRes review: getReviewRes) {
            if(review.getId() ==0){
                return new ArrayList<GetReviewByUserRes>();
            }
            review.setLikeCnt(getLikeCnt(review.getId()));
            review.setCommentCnt(getCommentCnt(review.getId()));
            review.setImgUrls(getReviewImgURLs(review.getId()));
            review.setFollowCnt(getFollowCnt(userId));
            review.setReviewCnt(getReviewCnt(userId));
            review.setIsLike(getIsLike(review.getId(), userIdxByJwt));
            review.setIsWish(getIsWish(review.getRestaurantId(), userIdxByJwt));
        }
        return getReviewRes;
    }

    private Integer getIsWish(Integer restaurantId, Integer userIdxByJwt) {
        String getIsWishQuery = "select exists (select * from wishes where restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getIsWishQuery, int.class, restaurantId, userIdxByJwt);
    }

    public GetReviewImageRes getReviewImages(Integer userId, List<Integer> foodCategories, String sortOption, Integer userIdxByJwt, Double latitude, Double longitude) {
        GetUserInfo getUserInfo = getUserInfo(userId);

        String getReviewImageQuery = "select IR.id , R.id, RT.name , IR.img_url, U.id, U.user_name, U.profile_img_url, R.content, date_format(IR.updated_at, '%Y-%m-%d') " +
                "from images_review as IR " +
                "join reviews as R " +
                "on IR.review_id = R.id " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "left join (SELECT * FROM (" +
                " SELECT ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n" +
                " FROM restaurants) DATA) as D " +
                "on RT.id = D.id "+
                "where R.user_id = ? and IR.status = 'ACTIVE' and RT.food_category in(";

        Object[] params = new Object[foodCategories.size()+4];
        params[0] = latitude;
        params[1] = longitude;
        params[2] = latitude;
        params[3] = userId;

        for(int i=0; i<foodCategories.size(); i++) {
            params[i+4] = foodCategories.get(i);
            getReviewImageQuery+="?,";
        }

        getReviewImageQuery = getReviewImageQuery.substring(0, getReviewImageQuery.length()-1);

        if(sortOption.equals("distance")) {
            getReviewImageQuery+=") order by D."+sortOption;
        }else {
            getReviewImageQuery+=") order by R."+sortOption+ " desc";
        }

        List<GetReviewImage> getReviewImages = jdbcTemplate.query(getReviewImageQuery,
                (rs, rowNum) -> new GetReviewImage(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)
                ), params);

        for(GetReviewImage reviewImage : getReviewImages) {
            reviewImage.setIsLike(getIsLike(reviewImage.getReviewId(), userIdxByJwt));
        }

        return new GetReviewImageRes(getUserInfo, getReviewImages);


    }

    public GetReviewImageRes getReviewImages(Integer userId, List<Integer> foodCategories, String sortOption, Integer userIdxByJwt) {
        GetUserInfo getUserInfo = getUserInfo(userId);

        String getReviewImageQuery = "select IR.id , R.id, RT.name, IR.img_url, U.id, U.user_name, U.profile_img_url, R.content, date_format(IR.updated_at, '%Y-%m-%d') " +
                "from images_review as IR " +
                "join reviews as R " +
                "on IR.review_id = R.id " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where R.user_id = ? and IR.status = 'ACTIVE' and RT.food_category in(";

        Object[] params = new Object[foodCategories.size()+1];
        params[0] = userId;
        for(int i=0; i<foodCategories.size(); i++) {
            params[i+1] = foodCategories.get(i);
            getReviewImageQuery+="?,";
        }

        getReviewImageQuery = getReviewImageQuery.substring(0, getReviewImageQuery.length()-1);
        getReviewImageQuery+=") order by R."+sortOption+" desc";

        List<GetReviewImage> getReviewImages = jdbcTemplate.query(getReviewImageQuery,
                (rs, rowNum) -> new GetReviewImage(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9)
                ), params);

        for(GetReviewImage reviewImage : getReviewImages) {
            reviewImage.setIsLike(getIsLike(reviewImage.getReviewId(), userIdxByJwt));
        }

        return new GetReviewImageRes(getUserInfo, getReviewImages);

    }

    private GetUserInfo getUserInfo(Integer userIdxByJwt) {
        String getUserInfoQuery = "select id, user_name, profile_img_url, " +
                "(select count(*) from follows where user_id = ?) as follow, " +
                "(select count(*) from reviews where user_id = ?) as review " +
                " from users " +
                " where id = ? and status = 'ACTIVE'";

        return jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)
                ), userIdxByJwt, userIdxByJwt, userIdxByJwt);
    }

    public int checkReviewImg(Integer imgId, Integer userId) {
        String checkReviewImgQuery = "select exists ( " +
                "select I.*, R.* " +
                "from images_review as I " +
                "join reviews as R " +
                "on I.review_id = R.id " +
                "where I.id = ? and I.status = 'ACTIVE' and R.user_id = ?)";

        return jdbcTemplate.queryForObject(checkReviewImgQuery, int.class, imgId, userId);
    }

    public int deleteReviewImgByUser(Integer imgId) {
        String deleteReviewImgQuery = "update images_review set status = 'INACTIVE' where id = ? ";
        return jdbcTemplate.update(deleteReviewImgQuery, imgId);
    }

    public GetNewsRes getReviewToday(Integer userId) {
        String reviewTodayQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, \n" +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, R.updated_at \n" +
                "from reviews as R\n" +
                "left join users as U\n" +
                "on R.user_id = U.id \n" +
                "left join restaurants as RT \n" +
                "on R.restaurant_id = RT.id\n" +
                "left join (select max(A.count + B.count), A.user_id as user_id\n" +
                "from (select count(*) as count, user_id from reviews group by user_id) as A,\n" +
                "(select count(*) as count, user_id from follows group by user_id) as B\n" +
                "where A.user_id = B.user_id) as AB\n" +
                "on AB.user_id = R.user_id\n" +
                "where R.status = 'ACTIVE' and R.updated_at like ? order by R.updated_at DESC " +
                "limit 1";

        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = now.format(formatter)+"%";
       //String currentDate = "2022-05-23";

        GetNewsRes getReviewTodayRes = null;

        try {
             getReviewTodayRes = jdbcTemplate.queryForObject(reviewTodayQuery,
                    (rs, rowNum) -> new GetNewsRes(
                            rs.getInt(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5),
                            rs.getString(6),
                            rs.getInt(7),
                            rs.getString(8),
                            rs.getInt(9),
                            rs.getString(10)
                    ), currentDate);


             getReviewTodayRes.setWish(getWish(userId, getReviewTodayRes.getRestaurantId()));
             getReviewTodayRes.setLike(getLike(userId, getReviewTodayRes.getReviewId()));

            getReviewTodayRes.setFollowCnt(getFollowCnt(userId));
            getReviewTodayRes.setReviewCnt(getReviewCnt(userId));
            getReviewTodayRes.setReviewLikeCnt(getLikeCnt(getReviewTodayRes.getReviewId()));
            getReviewTodayRes.setReviewCommentCnt(getCommentCnt(getReviewTodayRes.getReviewId()));
            getReviewTodayRes.setImgUrls(getReviewImgURLs(getReviewTodayRes.getReviewId()));
            getReviewTodayRes.setComments(getComments(getReviewTodayRes.getReviewId()));

            return getReviewTodayRes;
        }catch (EmptyResultDataAccessException e) {
            return getReviewTodayRes;
        }

    }

    private int getLike(Integer userId, Integer reviewId) {
        String getLikeQuery = "select exists (select * from likes where user_id = ? and review_id = ? )";
        return jdbcTemplate.queryForObject(getLikeQuery, int.class, userId, reviewId);
    }

    private int getWish(Integer userId, Integer restaurantId) {
        String getWishQuery = "select exists (select * from wishes where user_id = ? and restaurant_id = ?)";
        return jdbcTemplate.queryForObject(getWishQuery, int.class, userId, restaurantId);
    }

    public List<GetNewsRes> getNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
                "where R.status = 'ACTIVE' and R.score IN(";

        Object[] params = new Object[scores.size()];
        for(int i=0; i<scores.size(); i++) {
            params[i] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.created_at desc";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                rs.getInt(1),
                rs.getInt(2),
                rs.getString(3),
                rs.getString(4),
                rs.getInt(5),
                rs.getString(6),
                rs.getInt(7),
                rs.getString(8),
                rs.getInt(9),
                rs.getString(10)
        ), params);

        for(GetNewsRes news : getNewsRes) {
            if (userId != 0) {
                news.setWish(getWish(userId, news.getRestaurantId()));
                news.setLike(getLike(userId, news.getReviewId()));
            }
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
        }
        return getNewsRes;
    }

    public List<GetNewsRes> getHolicNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
               // "join (select * from follows where  follower_id= ?) as F " +
               // "on R.user_id = F.user_id " +
                "where R.status = 'ACTIVE' and U.is_holic = 1 and R.score IN(";

        Object[] params = new Object[scores.size()];
        //params[0] = userId;
        for(int i=0; i<scores.size(); i++) {
            params[i] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.updated_at desc ";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getString(10)
                ), params);

        for(GetNewsRes news : getNewsRes) {
            news.setWish(getWish(userId, news.getRestaurantId()));
            news.setLike(getLike(userId, news.getReviewId()));
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
            news.setReviewLikeCnt(getLikeCnt(news.getReviewId()));
            news.setReviewCommentCnt(getCommentCnt(news.getReviewId()));
        }
        return getNewsRes;
    }

    public List<GetNewsRes> getFollowNews(Integer userId, List<Integer> scores) {
        String getNewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, " +
                "U.profile_img_url, R.restaurant_id, RT.name , U.is_holic, date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id  " +
                "join restaurants as RT  " +
                "on R.restaurant_id = RT.id " +
                "join (select * from follows where  follower_id= ?) as F " +
                "on R.user_id = F.user_id " +
                "where R.status = 'ACTIVE' and R.score IN(";

        Object[] params = new Object[scores.size()+1];
        params[0] = userId;
        for(int i=0; i<scores.size(); i++) {
            params[i+1] = scores.get(i);
            getNewsQuery+="?,";
        }

        getNewsQuery = getNewsQuery.substring(0, getNewsQuery.length()-1);
        getNewsQuery+=") order by R.updated_at desc ";

        List<GetNewsRes> getNewsRes = jdbcTemplate.query(getNewsQuery,
                (rs, rowNum) -> new GetNewsRes(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getString(6),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getString(10)
                ), params);

        for(GetNewsRes news : getNewsRes) {
            news.setWish(getWish(userId, news.getRestaurantId()));
            news.setLike(getLike(userId, news.getReviewId()));
            news.setFollowCnt(getFollowCnt(userId));
            news.setReviewCnt(getReviewCnt(userId));
            news.setImgUrls(getReviewImgURLs(news.getReviewId()));
            news.setComments(getComments(news.getReviewId()));
            news.setReviewLikeCnt(getLikeCnt(news.getReviewId()));
            news.setReviewCommentCnt(getCommentCnt(news.getReviewId()));
        }
        return getNewsRes;
    }

    public int checkReviewId(int reviewId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId);
    }



}
