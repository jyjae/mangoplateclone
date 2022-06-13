package com.example.demo.src.mylist;

import com.example.demo.src.mylist.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyListDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<GetMyListRes> getMyList(int userId){
        String getMyListQuery = "select m2.id, m2.title, m2.content,\n" +
                "(select r.img_url from mylist_restaurant m\n" +
                "    inner join images_restaurant r on m.restaurant_id = r.id where m.mylist_id = m2.id limit 1) as imgUrl,\n" +
                "(select COUNT(*)\n" +
                "from bookmarks b\n" +
                "where b.content_type = 'mylists' and b.content_id = m2.id and b.status = 'ACTIVE') as bookmarkCount\n" +
                "    from mylists m2 where m2.user_id = ? and status='ACTIVE'";
        return this.jdbcTemplate.query(getMyListQuery,
                (rs,rowNum) -> new GetMyListRes(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("imgUrl"),
                        rs.getInt(5)), userId);
    }

    public GetMyListDetailRes getMyListDetail(int targetUserId, int myListId, int userId){
        String getMyListQuery = "select m.id, DATE_FORMAT(m.created_at,'%Y-%m-%d') as createdAt, m.view, m.title, m.user_id as userId, u.user_name as userName, u.profile_img_url as profileImgUrl,\n" +
                "       (select COUNT(*) from reviews r where r.user_id = m.user_id) as numReviews,\n" +
                "       (select COUNT(*) from follows f where f.user_id = m.user_id) as numFollowers,\n" +
                "       m.content,\n" +
                "       (select COUNT(*) from bookmarks b where b.content_type = 'mylists' and b.content_id = m.id and b.status = 'ACTIVE') as bookmarkCount,\n" +
                "       (select count(*) from mylist_restaurant mr where mr.mylist_id = m.id and mr.status ='ACTIVE') as restaurantsCount\n" +
                "from mylists m\n" +
                "join users u on u.id = m.user_id\n" +
                "where m.status = 'ACTIVE' and m.id = ?";
        GetMyListDetailRes getMyListDetailRes = this.jdbcTemplate.queryForObject(getMyListQuery,
                (rs,rowNum) -> new GetMyListDetailRes(
                        rs.getInt("id"),
                        rs.getString("createdAt"),
                        rs.getInt("view") + 1,
                        rs.getInt("bookmarkCount"),
                        rs.getInt("restaurantsCount"),
                        rs.getString("title"),
                        rs.getInt("userId"),
                        rs.getString("userName"),
                        rs.getString("profileImgUrl"),
                        rs.getInt("numReviews"),
                        rs.getInt("numFollowers"),
                        rs.getString("content"))
                        , myListId);
        getMyListDetailRes.setRestaurants(getRestaurants(userId, getMyListDetailRes.getMylistId()));
        updateView(getMyListDetailRes.getView(), getMyListDetailRes.getMylistId());
        return getMyListDetailRes;
    }

    public List<SubRestaurantInfo> getRestaurants(int userId, int myListId) {
        String getRestaurantsQuery = "select *\n" +
                "from mylist_restaurant m\n" +
                "                join (select r.id as restaurantId,\n" +
                "                r.status as restaurantStatus,\n" +
                "                ir.img_url as imgUrl,\n" +
                "                r.name as restaurantName,\n" +
                "                r.address as address,\n" +
                "                round((select AVG(rv.score) from reviews rv where rv.restaurant_id = r.id),1) as ratingsAvg,\n" +
                "                r2.reviewId,\n" +
                "                r2.reviewUserId,\n" +
                "                r2.reviewUserProfileImg,\n" +
                "                r2.reviewUserName,\n" +
                "                r2.reviewContent,\n" +
                "                (select exists(select * from wishes w where w.status = 'ACTIVE' and w.user_id = ? and w.restaurant_id = r.id))as isWishes,\n" +
                "                (select exists(select * from visits v where v.status = 'ACTIVE' and v.user_id = ? and v.restaurant_id = r.id))as isVisits\n" +
                "                from restaurants r\n" +
                "                left join (select r3.user_id as reviewUserId,\n" +
                "                r3.id as reviewId, u.profile_img_url as reviewUserProfileImg,\n" +
                "                u.user_name as reviewUserName, r3.content as reviewContent, r3.restaurant_id\n" +
                "                from reviews r3\n" +
                "                inner join users u on r3.user_id = u.id\n" +
                "                order by r3.created_at desc ) as r2 on r.id = r2.restaurant_id\n" +
                "                inner join images_restaurant ir on r.id = ir.restaurant_id group by r.id )as A on m.restaurant_id = A.restaurantId\n" +
                "                where m.status = 'ACTIVE' and m.mylist_id = ?";

        List<SubRestaurantInfo> getRestaurantInfo = this.jdbcTemplate.query(getRestaurantsQuery,
                (rs, rowNum) -> new SubRestaurantInfo(
                        rs.getInt("restaurantId"),
                        rs.getString("restaurantStatus"),
                        rs.getString("imgUrl"),
                        rs.getString("restaurantName"),
                        rs.getString("address"),
                        rs.getDouble("ratingsAvg"),
                        rs.getInt("isWishes"),
                        rs.getInt("isVisits"),
                        rs.getInt("reviewId"),
                        rs.getInt("reviewUserID"),
                        rs.getString("reviewUserProfileImg"),
                        rs.getString("reviewUserName"),
                        rs.getString("reviewContent"))
                , userId, userId, myListId);
        return getRestaurantInfo;
    }

    public int insert2MyList(Integer restaurantId, Integer myListId) {
        String insert2MyListQuery = "INSERT INTO mylist_restaurant (mylist_id, restaurant_id, status, created_at, updated_at) VALUES (?, ?, DEFAULT, DEFAULT, DEFAULT)";
        return jdbcTemplate.update(insert2MyListQuery, myListId, restaurantId);
    }
    public int checkMyList(Integer userId) {
        String checkMyListQuery = "select exists (select * from mylists where user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListQuery, int.class, userId);
    }

    public int checkMyListId(Integer myListId) {
        String checkMyListIdQuery = "select exists (select * from mylists where id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListIdQuery, int.class, myListId);
    }
    public int checkMyListEmpty(Integer myListId) {
        String checkMyListIdQuery = "select exists (select * from mylist_restaurant where mylist_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListIdQuery, int.class, myListId);
    }

    public int checkUserMyListId(Integer myListId, Integer userId) {
        String checkMyListIdQuery = "select exists (select * from mylists where id = ? and user_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkMyListIdQuery, int.class, myListId, userId);
    }

    public int checkDuplicated(Integer myListId, Integer restaurantId) {
        String checkDuplicatedQuery = "select exists (select * from mylist_restaurant where mylist_id = ? and restaurant_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkDuplicatedQuery, int.class, new Object[]{myListId, restaurantId});
    }

    public Integer createMyList(PostMyListReq postMyListReq, Integer userId) {
        String createMyListQuery = "insert into mylists(title, content, view, status, created_at, updated_at, user_id) " +
                    "values(?, ?, DEFAULT, DEFAULT, DEFAULT, DEFAULT, ?)";

        Object[] createMyListParams = new Object[] {postMyListReq.getTitle(), postMyListReq.getContent(), userId};
        this.jdbcTemplate.update(createMyListQuery, createMyListParams);

        String lastInsertIdQuery = "select id\n" +
                "from mylists\n" +
                "order by created_at desc\n" +
                "limit 1";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery,int.class);

    }

    public int updateMyList(PutMyListReq putMyListReq) {
        String updateMyListQuery = "update mylists set title = ?, content = ? where id = ?;\n";
        return jdbcTemplate.update(updateMyListQuery, putMyListReq.getTitle(), putMyListReq.getContent(), putMyListReq.getMyListId());
    }

    public int deleteMyList(int myListId) {
        String deleteMyListQuery = "update mylists set status = 'INACTIVE' where id = ?";
        return jdbcTemplate.update(deleteMyListQuery, myListId);
    }
    public int deleteAllRestaurants(int myListId) {
        try{
            String deleteAllQuery = "update mylist_restaurant set status = case mylist_id when ? then 'INACTIVE' END where mylist_id = ?";
            return jdbcTemplate.update(deleteAllQuery, myListId, myListId);
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return 0;
        }}

    public int deleteRestaurants(int restaurantId, int myListId) {
        try{
            String deleteAllQuery = "update mylist_restaurant set status = 'INACTIVE' where mylist_id = ? and restaurant_id = ?";
            return jdbcTemplate.update(deleteAllQuery, myListId, restaurantId);
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return 0;
        }}
    public void updateView(Integer view , Integer myListId) {
        String updateView = "update mylists set view = ? where id = ?;\n";
        this.jdbcTemplate.update(updateView, view, myListId);
    }
    public int  checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public int checkRestaurantId(int restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }
}
