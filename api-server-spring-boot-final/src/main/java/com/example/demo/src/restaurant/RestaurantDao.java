package com.example.demo.src.restaurant;

import com.example.demo.src.comment.model.GetCommentRes;

import com.example.demo.src.restaurant.model.*;
import com.example.demo.src.review.model.GetReviewRes;

import com.example.demo.src.comment.model.GetSubComment;
import com.example.demo.src.menu.model.GetRestaurantMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import java.util.ArrayList;
import java.util.Optional;


@Repository
public class RestaurantDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories, int range, String orderOption, Integer userId) {
            String getRestaurantQuery = "select R.id,\n" +
                    "       R.name,\n" +
                    "       R.address,\n" +
                    "       rgn.name as regionName,\n" +
                    "       cf.name as foodCategory,\n" +
                    "       R.latitude,\n" +
                    "       R.longitude,\n" +
                    "       (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl,\n" +
                    "       (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews,\n" +
                    "       ROUND(D.distance,2) as distance,\n" +
                    "       ROUND((select avg(Rev.score) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE'),1)as ratingsAvg,\n" +
                    "       R.view,\n" +
                    "       (select exists(select * from wishes w where w.status = 'ACTIVE' and w.user_id = ? and w.restaurant_id = R.id))as isWishes,\n" +
                    "       (select exists(select * from visits v where v.status = 'ACTIVE' and v.user_id = ? and v.restaurant_id = R.id))as isVisits\n" +
                    "       from restaurants as R\n" +
                    "        join (SELECT * FROM (\n" +
                    "                SELECT ( 6371 * acos( cos( radians( ?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n" +
                    "            FROM restaurants) DATA\n" +
                    "           WHERE DATA.distance < ?) as D\n" +
                    "       on R.id = D.id\n" +
                    "       inner join regions rgn on R.region = rgn.id\n" +
                    "       inner join categories_food cf on R.food_category = cf.id" +
                    "       where R.status = 'ACTIVE' and R.food_category in " + foodCategories +
                    "       order by " + orderOption;
            Object[] params = new Object[] {userId,userId,latitude, longitude, latitude, range};
            List<GetRestaurantRes> getRestaurantRes = this.jdbcTemplate.query(getRestaurantQuery,
                    (rs, rowNum) -> new GetRestaurantRes(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("regionName"),
                            rs.getString("foodCategory"),
                            rs.getDouble("latitude"),
                            rs.getDouble("longitude"),
                            rs.getInt("numReviews"),
                            rs.getDouble("ratingsAvg"),
                            rs.getDouble("distance"),
                            rs.getInt("isWishes"),
                            rs.getInt("isVisits"),
                            rs.getInt("view"),
                            rs.getString("address"),
                            rs.getString("imgUrl"))
            ,params );

            getRestaurantRes.forEach(s -> s.setRegionName(extractRegionName(s.getAddress())));
            return getRestaurantRes;
    }

    public List<GetRestaurantRes> getRestaurant(String regionCode, String foodCategories, String orderOption, Integer userId) {
        System.out.println(regionCode);
        String getRestaurantQuery = "select R.id,\n" +
                "       R.name,\n" +
                "       R.address,\n" +
                "       rgn.name as regionName,\n" +
                "       cf.name as foodCategory,\n" +
                "       R.latitude,\n" +
                "       R.longitude,\n" +
                "       (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews,\n" +
                "       ROUND((select avg(Rev.score) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE'),1)as ratingsAvg,\n" +
                "       R.view,\n" +
                "       (select exists(select * from wishes w where w.status = 'ACTIVE' and w.user_id = ? and w.restaurant_id = R.id))as isWishes,\n" +
                "       (select exists(select * from visits v where v.status = 'ACTIVE' and v.user_id = ? and v.restaurant_id = R.id))as isVisits,\n" +
                "       (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl\n" +
                "from restaurants as R\n" +
                "inner join regions rgn on R.region = rgn.id\n" +
                "inner join categories_food cf on R.food_category = cf.id" +
                " where R.status = 'ACTIVE' and R.food_category in "+ foodCategories + "and R.region in " + regionCode +
                " order by " + orderOption;
        Object[] params = new Object[] {userId,userId};
        List<GetRestaurantRes> getRestaurantRes = this.jdbcTemplate.query(getRestaurantQuery,
                (rs, rowNum) -> new GetRestaurantRes(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("regionName"),
                        rs.getString("foodCategory"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("numReviews"),
                        rs.getDouble("ratingsAvg"),
                        rs.getInt("isWishes"),
                        rs.getInt("isVisits"),
                        rs.getInt("view"),
                        rs.getString("address"),
                        rs.getString("imgUrl")),params);
        System.out.println(getRestaurantRes.size());
        System.out.println(getRestaurantRes.toString());
        getRestaurantRes.forEach(s -> s.setRegionName(extractRegionName(s.getAddress())));

        return getRestaurantRes;
    }

    public int checkRestaurantId(int restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public GetRestaurantDetailRes getRestaurantDetail(Integer restaurantId) {
        String getRestaurantQuery = " select R.id, R.name, R.view, R.address, R.latitude, R.longitude, R.day_off, R.open_hour, R.close_hour, R.break_time, R.min_price, R.max_price, R.park_info, R.website, R.food_category, C.name, date_format(R.updated_at, '%Y-%m-%d') " +
                "from restaurants as R " +
                "join categories_food as C " +
                "on R.food_category = C.id " +
                "where R.id = ? and R.status = 'ACTIVE'";

        GetRestaurantDetailRes getRestaurantDetailRes = null;

        try {
            getRestaurantDetailRes = jdbcTemplate.queryForObject(getRestaurantQuery,
                    (rs, rowNum) -> new GetRestaurantDetailRes(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getString(4),
                            rs.getDouble(5),
                            rs.getDouble(6),
                            rs.getString(7),
                            rs.getString(8),
                            rs.getString(9),
                            rs.getString(10),
                            rs.getInt(11),
                            rs.getInt(12),
                            rs.getString(13),
                            rs.getString(14),
                            rs.getInt(15),
                            rs.getString(16),
                            rs.getString(17)
                    ), restaurantId);

            getRestaurantDetailRes.setImgUrls(getRestaurantImgUrls(restaurantId));
            getRestaurantDetailRes.setReviews(getReviews(restaurantId));
            getRestaurantDetailRes.setScore(getRestaurantScore(restaurantId));
            getRestaurantDetailRes.setMenus(getRestaurantMenus(restaurantId));
            getRestaurantDetailRes.setWishCnt(getWishCnt(restaurantId));

        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return getRestaurantDetailRes;

    }

    private int getWishCnt(int restaurantId) {
        String getWishCntQuery = "select count(*) from wishes where restaurant_id = ? and status ='ACTIVE'";
        return jdbcTemplate.queryForObject(getWishCntQuery, int.class, restaurantId);
    }

    private List<String> getRestaurantImgUrls(Integer restaurantId) {
        String getRestaurantImgUrlQuery = "select img_url from images_restaurant where restaurant_id = ?";
        List<String> imgUrls = new ArrayList<>();

        jdbcTemplate.query(getRestaurantImgUrlQuery,
                (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), restaurantId);

        return imgUrls;
    }

    public List<GetReviewRes> getReviews(int restaurantId) {
        String getReviewsQuery = "select R.id, R.user_id, U.user_name, R.content, R.score, U.profile_img_url, R.restaurant_id, RT.name, U.is_holic,  date_format(R.updated_at, '%Y-%m-%d') " +
                "from reviews as R " +
                "join users as U " +
                "on R.user_id = U.id " +
                "join restaurants as RT " +
                "on R.restaurant_id = RT.id " +
                "where RT.id = ? and R.status ='ACTIVE' ";

        List<GetReviewRes> getReviewRes = jdbcTemplate.query(getReviewsQuery,
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
                ), restaurantId);

        for(GetReviewRes review : getReviewRes) {
            //List<GetCommentRes> commentRes = getComments(review.getId());
            List<String> imgUrls = getReviewImgURLs(review.getId());
            //review.setComments(commentRes);
            review.setImgUrls(imgUrls);
            review.setReviewCnt(getReviewCnt(review.getUserId()));
            review.setFollowCnt(getFollowCnt(review.getUserId()));
        }

        return getReviewRes;

    }

    private int getFollowCnt(int userId) {
        String getFollowCntQuery = "select count(*) from follows where user_id = ?";
        return jdbcTemplate.queryForObject(getFollowCntQuery, int.class, userId);
    }

    private int getReviewCnt(int userId) {
        String getReviewCntQuery = "select count(*) from reviews where user_id = ?";
        return jdbcTemplate.queryForObject(getReviewCntQuery, int.class, userId);
    }

    public Float getRestaurantScore(int restaurantId) {
        String getScoreQuery = "select ifNull(avg(score),0) from reviews where restaurant_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getScoreQuery, Float.class, restaurantId);
    }

    public List<String> getReviewImgURLs(int reviewId) {
        String getReviewImgQuery = "select img_url from images_review where review_id = ? and status ='ACTIVE'";
        List<String> imgUrls = new ArrayList<>();

        jdbcTemplate.query(getReviewImgQuery,
                (rs, rowNum) -> imgUrls.add(rs.getString("img_url")), reviewId);
        return imgUrls;
    }

    public List<GetCommentRes> getComments(int reviewId) {
        String getComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.is_holic, date_format(C.updated_at, '%Y-%m-%d'), U.profile_img_url " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id and C.review_id = ? " +
                "where level = 1 " +
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
            List<GetSubComment> subComments = getSubComments(comment.getId());
            comment.setSubComments(subComments);
        }
        return getCommentRes;
    }

    private List<GetSubComment> getSubComments(int groupNum) {
        String getSubComments = "select C.id, C.user_id, U.user_name, C.comment, `order`, U.profile_img_url, U.is_holic, C.updated_at " +
                "from review_comments as C " +
                "join users as U " +
                "on C.user_id = U.id " +
                "where level > 1 and group_num = ? and status = 'ACTIVE' " +
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

    public int increaseView(Integer restaurantId) {
        String increaseViewQuery = "update restaurants " +
                "set view = " +
                "(select viewer from " +
                "(select view+1 as viewer from restaurants where id = ?) A) " +
                "where id = ?";

        return jdbcTemplate.update(increaseViewQuery, restaurantId, restaurantId);
    }

    public List<GetRestaurantMenu> getRestaurantMenus(Integer restaurantId) {
        String getMenusQuery = "select id, name, price from menus where restaurant_id = ?";
        return jdbcTemplate.query(getMenusQuery,
                (rs, rowNum) -> new GetRestaurantMenu(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("price")
                ), restaurantId);
    }

    public List<GetMyRestaurantsRes> getMyRestaurants(Integer userId) {
        String getMyRestaurantsQuery = "select r.id, r.name, r.address, c.name, DATE_FORMAT(r.created_at,'%Y-%m-%d')\n" +
                    "from restaurants r\n" +
                    "join categories_food c on r.food_category = c.id\n" +
                    "where r.user_id = ? and r.status = 'ACTIVE'";
        List<GetMyRestaurantsRes> getMyRestaurants = jdbcTemplate.query(getMyRestaurantsQuery,
                    (rs, rowNum) -> new GetMyRestaurantsRes(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5)), userId);
        return getMyRestaurants;
    }

    public PostRestaurantRes createRestaurant(PostRestaurantReq postRestaurantReq, Integer userId) {
        String createRestaurantQuery =
                "INSERT INTO restaurants (name, view, address, first_region_id, second_region_id, third_region_id, latitude, longitude, open_hour, " +
                        "close_hour, break_time, min_price, max_price, day_off, park_info, last_order, website, status, created_at, updated_at, food_category, user_id, store_number) " +
                        "VALUES ( ?, 0, ?, null, null, null, ?, ?, null, null, null, null, null, null, DEFAULT, null, null, DEFAULT, DEFAULT, DEFAULT, ?, ?, ?)\n";
//        food_category default 값 설정 필요.
        Object[] createRestaurantParams = new Object[]{postRestaurantReq.getName(), postRestaurantReq.getAddress(), postRestaurantReq.getLatitude(),
                postRestaurantReq.getLongitude(), Optional.ofNullable(postRestaurantReq.getFoodCategory()).orElse(null), userId, postRestaurantReq.getStoreNumber()};
        this.jdbcTemplate.update(createRestaurantQuery, createRestaurantParams);

        String lastInsertIdQuery = "select id, name, address, concat(DATEDIFF(NOW(),created_at),'일 전')as 'createdAt'\n" +
                "from restaurants\n" +
                "where id = (select last_insert_id())";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,
                (rs,rowNum)-> new PostRestaurantRes(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("createdAt")
        ));
    }

    public Integer findByNameAndAddress(PostRestaurantReq postRestaurantReq){
        String findByNameAndAddressQuery = "select exists(select id from restaurants where name = ? and address = ? and status = 'ACTIVE')";
        Object[] findByNameAndAddressParams = new Object[]{postRestaurantReq.getName(), postRestaurantReq.getAddress()};
        return this.jdbcTemplate.queryForObject(findByNameAndAddressQuery,
                int.class,
                findByNameAndAddressParams);
    }
    public Integer deleteRestaurant(Integer restaurantId){
        String deleteRestaurantQuery = "UPDATE restaurants r SET r.status = 'INACTIVE' WHERE r.id = ?";
        Integer deleteRestaurantParams = restaurantId;
        return this.jdbcTemplate.update(deleteRestaurantQuery, deleteRestaurantParams);
    }

    public Integer updateRestaurant(Integer restaurantId, PutRestaurantReq putRestaurantReq) {
        String updateRestaurantQuery = "UPDATE restaurants t SET t.name = ?, t.address = ?, t.latitude = ?, t.longitude = ?, t.food_category = ? WHERE t.id = ?";
        Object[] updateRestaurantParams = new Object[]{putRestaurantReq.getName(), putRestaurantReq.getAddress(), putRestaurantReq.getLatitude(),
                putRestaurantReq.getLongitude(), Optional.ofNullable(putRestaurantReq.getFoodCategory()).orElse(null), restaurantId};
        return this.jdbcTemplate.update(updateRestaurantQuery, updateRestaurantParams);
    }

    public int checkMyRestaurant(Integer restaurantId, Integer userId) {
        String checkUserQuery = "select exists (select * from restaurants where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, restaurantId, userId);
    }
    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }
    public int updateRestaurantName(String value, Integer userId) {
        String checkUserQuery = "update restaurants set name = ? where user_id = ?";
        return jdbcTemplate.update(checkUserQuery, value, userId);
    }
    public int updateRestaurantAddress(String address, Double latitude, Double longitude, Integer userId) {
        String checkUserQuery = "update restaurants set address = ?,  latitude = ?, longitude = ?where user_id = ?";
        return jdbcTemplate.update(checkUserQuery, address, latitude, longitude, userId);
    }
    public int updateRestaurantFoodCategory(Integer value, Integer userId) {
        String checkUserQuery = "update restaurants set food_category = ? where user_id = ?";
        return jdbcTemplate.update(checkUserQuery, value, userId);
    }

    public String extractRegionName(String address){
        String[] addressInfo = address.split(" ");

//        for(String s : addressInfo)
//            System.out.print(s+",");
        if(addressInfo[0].equals("서울특별시")) {
//            System.out.println(addressInfo[2]);
            return addressInfo[2];
        } else {
//            System.out.println(addressInfo[1]);
            return addressInfo[1];
        }
    }


}
