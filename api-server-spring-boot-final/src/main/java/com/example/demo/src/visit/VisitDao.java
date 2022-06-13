package com.example.demo.src.visit;

import com.example.demo.config.BaseResponse;
import com.example.demo.src.visit.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class VisitDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkRestaurant(Integer restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ?)";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public Integer createVisit(int restaurantId, int userId) {
        String createVisitQuery = "insert into visits(restaurant_id, user_id, status) " +
                "values(?,?,'ACTIVE')";
        jdbcTemplate.update(createVisitQuery, restaurantId, userId);

        String getLastIdQuery = "select id from visits order by id desc limit 1";
        return jdbcTemplate.queryForObject(getLastIdQuery, int.class);
    }

    public int checkUser(Integer userIdxByJwt) {
        String checkUserQuery = "select exists (select * from users where id = ?)";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdxByJwt);
    }

    public int getVisitCheck(Integer restaurantId, Integer userIdxByJwt) {
        String getVisitQuery = "select exists (select restaurant_id, user_id from visits where restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getVisitQuery, int.class, restaurantId, userIdxByJwt);
    }

    public int checkVisit(Integer restaurantId, Integer userId,Integer visitId) {
        String checkVisitQuery = "select exists (select * from visits where id = ? and restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkVisitQuery, int.class, visitId, restaurantId, userId);
    }

    public int deleteVisit(int reviewId) {
        String deleteVisitQuery = "update visits set status = 'INACTIVE' where id = ? and status = 'ACTIVE'";
        return jdbcTemplate.update(deleteVisitQuery, reviewId);
    }

    public GetVisitRes GetVisitRes(Integer restaurantId, Integer userIdxByJwt) {
        String getVisitQuery = "select id, count(*) as count from visits where restaurant_id = ? and user_id = ? and status = 'ACTIVE' group by user_id, restaurant_id ";
        return jdbcTemplate.queryForObject(getVisitQuery,
                (rs, rowNum) ->new GetVisitRes(rs.getInt("id"), rs.getInt("count")),
                restaurantId, userIdxByJwt);
    }


    public GetVisitByUserRes getVisitByUser(Integer userId,  List<Integer> foodCategories, String sortOption,  Integer userIdxByJwt) {
        String getVisitId = "select V.id, V.restaurant_id, V.content " +
                "from visits as V " +
                "left join visit_likes as VL " +
                "on V.id = VL.visit_id " +
                "left join visit_comments as VC " +
                "on V.id = VC.visit_id "+
                "join restaurants as RT " +
                "on V.restaurant_id = RT.id " +
                "where V.user_id = ? and V.status = 'ACTIVE' and RT.food_category in(";

        GetUserInfo getUserInfo = getUserInfo(userId);

        Object[] params = new Object[foodCategories.size()+1];
        params[0] = userId;
        for(int i=0; i<foodCategories.size(); i++) {
            params[i+1] = foodCategories.get(i);
            getVisitId+="?,";
        }

        getVisitId = getVisitId.substring(0, getVisitId.length()-1);
        getVisitId+=") order by V."+sortOption+" desc";

        List<GetVisit> getVisits = jdbcTemplate.query(getVisitId,
                (rs, rowNum) -> new GetVisit(
                        rs.getInt("id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("content")
                ), params);
        
        for(GetVisit visit : getVisits) {
            if(visit.getVisitId() == 0) {
                return new GetVisitByUserRes(getUserInfo,new ArrayList<GetVisit>());
            }
            visit.setLikeCnt(getLikeCnt(visit.getVisitId()));
            visit.setCommentCnt(getCommentCnt(visit.getVisitId()));
            visit.setGetRestaurantInfo(getRestaurantInfo(visit.getRestaurantId()));
            visit.setIsWish(getWish(visit.getRestaurantId(), userIdxByJwt));
            visit.setIsLike(getLike(visit.getVisitId(), userIdxByJwt));
            visit.setComments(getComments(visit.getVisitId()));

        }

        return new GetVisitByUserRes(getUserInfo, getVisits);
    }

    private Integer getCommentCnt(Integer visitId) {
        String getCommentCnt = "select count(*) from visit_comments where visit_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getCommentCnt, int.class, visitId);
    }

    private Integer getLikeCnt(Integer visitId) {
        String getLikeCnt = "select count(*) from visit_likes where visit_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getLikeCnt, int.class, visitId);
    }

    private List<GetVisitComment> getComments(int visitId) {
        String getCommentsQuery = "select VC.id, VC.visit_id, U.id as user_id, U.user_name, VC.parentUserName, U.is_holic, VC.comment, VC.updated_at, U.profile_img_url " +
                "from visit_comments as VC " +
                "join users as U " +
                "on VC.user_id = U.id " +
                "where VC.visit_id = ?";
        return jdbcTemplate.query(getCommentsQuery,
                (rs, rowNum) -> new GetVisitComment(
                        rs.getInt("id"),
                        rs.getInt("visit_id"),
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("parentUserName"),
                        rs.getInt("is_holic"),
                        rs.getString("comment"),
                        rs.getString("updated_at"),
                        rs.getString("profile_img_url")
                ), visitId);
    }

    private int getLike(int visitId, Integer userIdxByJwt) {
        String getLikeQuery = "select exists (select * from visit_likes where visit_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getLikeQuery, int.class, visitId, userIdxByJwt);
    }

    private GetRestaurantInfo getRestaurantInfo(int restaurantId) {
        String getRestaurantInfoQuery = "select R.name, R.view, C.name, " +
                "(select img_url from images_restaurant where restaurant_id = ? limit 1) as img, " +
                "(select count(*) from reviews where restaurant_id = ?) as review " +
                " from restaurants as R " +
                " join categories_food as C " +
                " on R.food_category = C.id " +
                " where R.id = ?";

        GetRestaurantInfo getRestaurantInfo = null;
        try {
            getRestaurantInfo = jdbcTemplate.queryForObject(getRestaurantInfoQuery,
                    (rs, rowNum) -> new GetRestaurantInfo(
                            rs.getString(1),
                            rs.getInt(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getInt(5)

                    ), restaurantId, restaurantId, restaurantId);
            return getRestaurantInfo;
        }catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private GetUserInfo getUserInfo(Integer userIdxByJwt) {
        String getUserInfoQuery = "select id, user_name, profile_img_url, " +
                "(select count(*) from follows where user_id = ?) as follow, " +
                "(select count(*) from reviews where user_id = ?) as review " +
                " from users " +
                " where id = ?";

        return jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new GetUserInfo(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5)
                ), userIdxByJwt, userIdxByJwt, userIdxByJwt);
    }

    public int updateVisit(PutVisitReq putVisitReq, Integer userIdxByJwt) {
        String updateVisitQuery = "update visits set content = ? where id = ? and restaurant_id = ? and user_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.update(updateVisitQuery, putVisitReq.getContent(),putVisitReq.getVisitId(), putVisitReq.getRestaurantId(), userIdxByJwt);
    }

    public int checkTodayVisit(Integer restaurantId, Integer userIdxByJwt, String currentDate) {
        String checkTodayVisit = "select exists (select * from visits where updated_at like ? and restaurant_id =? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkTodayVisit, int.class, currentDate+"%", restaurantId, userIdxByJwt);
    }

    public GetVisitByUserRes getVisitByUser(Integer userId, List<Integer> foodCategories, String sortOption, Double latitude, Double longitude,  Integer userIdxByJwt) {
        String getVisitId = "select V.id, V.restaurant_id, V.content " +
                "from visits as V " +
                "left join visit_likes as VL " +
                "on V.id = VL.visit_id " +
                "left join visit_comments as VC " +
                "on V.id = VC.visit_id "+
                "join restaurants as RT " +
                "on V.restaurant_id = RT.id " +
                "left join (SELECT * FROM (" +
                " SELECT ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id " +
                " FROM restaurants) DATA) as D " +
                "on V.restaurant_id = D.id "+
                "where V.user_id = ? and V.status = 'ACTIVE' and RT.food_category in(";

        GetUserInfo getUserInfo = getUserInfo(userId);

        Object[] params = new Object[foodCategories.size()+4];
        params[0] = latitude;
        params[1] = longitude;
        params[2] = latitude;
        params[3] = userId;

        for(int i=0; i<foodCategories.size(); i++) {
            params[i+4] = foodCategories.get(i);
            getVisitId+="?,";
        }

        getVisitId = getVisitId.substring(0, getVisitId.length()-1);
        if(sortOption.equals("distance")) {
            getVisitId+=") order by D."+sortOption;
        }else {
            getVisitId+=") order by R."+sortOption+ " desc";
        }

        List<GetVisit> getVisits = jdbcTemplate.query(getVisitId,
                (rs, rowNum) -> new GetVisit(
                        rs.getInt("id"),
                        rs.getInt("restaurant_id"),
                        rs.getString("content")
                ), params);

        for(GetVisit visit : getVisits) {
            if(visit.getVisitId() == 0) {
                return new GetVisitByUserRes(getUserInfo,new ArrayList<GetVisit>());
            }
            visit.setLikeCnt(getLikeCnt(visit.getVisitId()));
            visit.setCommentCnt(getCommentCnt(visit.getVisitId()));
            visit.setGetRestaurantInfo(getRestaurantInfo(visit.getRestaurantId()));
            visit.setIsWish(getWish(visit.getRestaurantId(), userIdxByJwt));
            visit.setIsLike(getLike(visit.getVisitId(), userIdxByJwt));
            visit.setComments(getComments(visit.getVisitId()));
        }


        return new GetVisitByUserRes(getUserInfo, getVisits);
    }

    private int getWish(int restaurantId, Integer userIdxByJwt) {
        String getWishQuery = "select exists (select * from wishes where restaurant_id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(getWishQuery, int.class, restaurantId, userIdxByJwt);
    }
}
