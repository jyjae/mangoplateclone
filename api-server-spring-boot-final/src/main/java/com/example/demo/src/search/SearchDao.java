package com.example.demo.src.search;

import com.example.demo.src.restaurant.model.GetRestaurantRes;
import com.example.demo.src.search.model.GetSearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchDao {
    @Autowired private JdbcTemplate jdbcTemplate;

    public List<GetSearchRes> search(String search, Double latitude, Double longitude, int userId) {
        String searchQuery = "select R.id,\n" +
                " R.name,\n" +
                " R.address,\n" +
                " cf.name as foodCategory,\n" +
                "  R.latitude,\n" +
                "  R.longitude,\n" +
                "    (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews,\n" +
                "    ROUND((select avg(Rev.score) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE'),1)as ratingsAvg,\n" +
                "    R.view,\n" +
                "     (select exists(select * from wishes w where w.status = 'ACTIVE' and w.user_id = ? and w.restaurant_id = R.id))as isWishes,\n" +
                "     (select exists(select * from visits v where v.status = 'ACTIVE' and v.user_id = ? and v.restaurant_id = R.id))as isVisits,\n" +
                "     (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl,\n " +
                "      ROUND(D.distance,2) as distance\n" +
                "   from restaurants as R\n" +
                "        join (SELECT * FROM (\n" +
                "                SELECT ( 6371 * acos( cos( radians( ?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id\n" +
                "            FROM restaurants) DATA\n" +
                "          ) as D\n" +
                "       on R.id = D.id\n" +
                "   inner join categories_food cf on R.food_category = cf.id\n" +
                "   join menus as M\n" +
                "   on R.id = M.restaurant_id\n" +
                "   where R.status = 'ACTIVE' and (R.name like ? or R.address like ? or cf.name like ? or M.name = ?)\n" +
                "   group by R.id";

        String params = "%"+search+"%";
        Object[] searchParams = new Object[]{userId, userId, latitude, longitude,latitude, params, params, params, params};
        return jdbcTemplate.query(searchQuery,
                (rs, rowNum) -> new GetSearchRes(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("foodCategory"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("numReviews"),
                        rs.getDouble("ratingsAvg"),
                        rs.getInt("isWishes"),
                        rs.getInt("isVisits"),
                        rs.getInt("view"),
                        rs.getString("imgUrl"),
                        rs.getDouble("distance"))
                ,searchParams);
    }

    public List<GetSearchRes> search(String search) {
        String searchQuery = "select R.id,\n" +
                " R.name,\n" +
                " R.address,\n" +
                " cf.name as foodCategory,\n" +
                "  R.latitude,\n" +
                "  R.longitude,\n" +
                "    (select count(*) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE')as numReviews,\n" +
                "    ROUND((select avg(Rev.score) from reviews Rev where Rev.restaurant_id = R.id and Rev.status = 'ACTIVE'),1)as ratingsAvg,\n" +
                "    R.view,\n" +
                "     (select i.img_url from images_restaurant i where i.restaurant_id = R.id limit 1 )as imgUrl\n " +
                "   from restaurants as R\n" +
                "   inner join categories_food cf on R.food_category = cf.id\n" +
                "   join menus as M\n" +
                "   on R.id = M.restaurant_id\n" +
                "   where R.status = 'ACTIVE' and (R.name like ? or R.address like ? or cf.name like ? or M.name = ?)\n" +
                "   group by R.id";

        String params = "%"+search+"%";
        return jdbcTemplate.query(searchQuery,
                (rs, rowNum) -> new GetSearchRes(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("foodCategory"),
                        rs.getDouble("latitude"),
                        rs.getDouble("longitude"),
                        rs.getInt("numReviews"),
                        rs.getDouble("ratingsAvg"),
                        rs.getInt("view"),
                        rs.getString("imgUrl"))
                ,params,params,params,params);
    }

    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }
}
