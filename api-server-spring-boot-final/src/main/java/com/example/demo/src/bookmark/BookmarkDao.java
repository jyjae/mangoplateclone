package com.example.demo.src.bookmark;

import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import com.example.demo.src.bookmark.model.GetBookmarkedRes;
import com.example.demo.src.restaurant.model.GetRestaurantRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookmarkDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<GetBookmarkCountRes> getBookmarkCount(Integer userId) {
        try{
            String getBookmarkCountQuery = "select B.id,\n" +
                    "       B.content_type,\n" +
                    "       B.count,\n" +
                    "       IR.img_url,\n" +
                    "       B.user_id\n" +
                    "from (select *, count(*) as count from bookmarks where user_id = ? group by content_type) as B\n" +
                    "join mylist_restaurant as M\n" +
                    "on B.content_id = M.mylist_id\n" +
                    "join (select * from images_restaurant group by restaurant_id) as IR\n" +
                    "on IR.restaurant_id = M.restaurant_id\n" +
                    "where B.user_id = ?\n" +
                    "group by B.content_type";
            List<GetBookmarkCountRes> getBookmarkCountRes = this.jdbcTemplate.query(getBookmarkCountQuery,
                    (rs, rowNum) -> new GetBookmarkCountRes(
                            rs.getString(2),
                            rs.getInt(3),
                            rs.getString(4)), userId, userId);
            return getBookmarkCountRes;
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return null;
        }
    }
    public List<GetBookmarkedRes> getBookmarkedContents(Integer userId, String contentsType) {
        try{
            String getBookmarkedQuery = "";
            if(contentsType.equals("top_lists")) {
                getBookmarkedQuery = "select m.id, m.title, m.description, m.view, DATE_FORMAT(m.created_at,'%Y-%m-%d'), B.img_url\n" +
                        "from top_lists m\n" +
                        "join (select mr.top_list_id, IR.restaurant_id, IR.img_url\n" +
                        "from top_list_restaurants mr\n" +
                        "join (select img_url,restaurant_id from images_restaurant where status = 'ACTIVE' group by restaurant_id) as IR\n" +
                        "on IR.restaurant_id = mr.restaurant_id\n" +
                        "where mr.status = 'ACTIVE'\n" +
                        "group by top_list_id) as B on m.id = B.top_list_id\n" +
                        "join (select * from bookmarks where status = 'ACTIVE' and content_type = 'top_lists' and user_id = ?)as D\n" +
                        "on D.content_id = m.id\n" +
                        "where m.status = 'ACTIVE'";

            }else if(contentsType.equals("mylists")) {
                getBookmarkedQuery = "select m.id, m.title, m.content, m.view, DATE_FORMAT(m.created_at,'%Y-%m-%d'), B.img_url\n" +
                        "from mylists m\n" +
                        "join (select mr.mylist_id, IR.restaurant_id, IR.img_url\n" +
                        "from mylist_restaurant mr\n" +
                        "join (select img_url,restaurant_id from images_restaurant where status = 'ACTIVE' group by restaurant_id) as IR\n" +
                        "on IR.restaurant_id = mr.restaurant_id\n" +
                        "where mr.status = 'ACTIVE'\n" +
                        "group by mylist_id) as B on m.id = B.mylist_id\n" +
                        "join (select * from bookmarks where status = 'ACTIVE' and content_type = 'mylists' and user_id = ?)as D\n" +
                        "on D.content_id = m.id\n" +
                        "where m.status = 'ACTIVE'";
            }
            List<GetBookmarkedRes> getBookmarkedRes = this.jdbcTemplate.query(getBookmarkedQuery,
                    (rs, rowNum) -> new GetBookmarkedRes(
                            rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getLong(4),
                            rs.getString(5),1,
                            rs.getString(6)), userId);
            return getBookmarkedRes;
        }catch (Exception e){
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            return null;
        }
    }
    public int checkContentId(String contentsType, int contentsId) {
        String checkContentIdQuery = "select exists (select * from " + contentsType + " where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkContentIdQuery, int.class, contentsId);
    }

    public int checkBookmarked(int userId, String contentsType, int contentsId) {
        String checkBookmarkedQuery = "select exists (select * from bookmarks where user_id = ? and content_type = ? and content_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkBookmarkedQuery, int.class, userId, contentsType, contentsId);
    }
    public int checkUnmarked(int userId, String contentsType, int contentsId) {
        String checkUnmarkedQuery = "select exists (select * from bookmarks where user_id = ? and content_type = ? and content_id = ? and status ='INACTIVE')";
        return jdbcTemplate.queryForObject(checkUnmarkedQuery, int.class, userId, contentsType, contentsId);
    }


    public int createBookmark(int userId, String contentsType, int contentsId) {
        String createRelationQuery = "insert into bookmarks (user_id, content_id, content_type, status, created_at, updated_at) VALUES (?, ?, ?, DEFAULT, DEFAULT, DEFAULT)";
        return jdbcTemplate.update(createRelationQuery, userId, contentsId, contentsType);

    }

    public int postBookmark(int userId, String contentsType, int contentsId) {
        String postBookmarkQuery = "update bookmarks t SET t.status = 'ACTIVE' WHERE t.user_id = ? and t.content_type = ? and t.content_id = ? ";
        return jdbcTemplate.update(postBookmarkQuery, userId, contentsType, contentsId);
    }

    public int cancelBookmark(int userId, String contentsType, int contentsId) {
        String cancelBookmarkQuery = "update bookmarks t SET t.status = 'INACTIVE' WHERE t.user_id = ? and t.content_type = ? and t.content_id = ? ";
        return jdbcTemplate.update(cancelBookmarkQuery, userId, contentsType, contentsId);
    }
    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }
}
