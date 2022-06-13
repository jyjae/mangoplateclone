package com.example.demo.src.eatdeal;

import com.example.demo.src.eatdeal.model.GetEatDeal;
import com.example.demo.src.eatdeal.model.GetEatDealOrderRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.eatdeal.model.PostEatDealReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EatDealDao {
    @Autowired private JdbcTemplate jdbcTemplate;

    public List<GetEatDeal> getEatDeals(Double latitude, Double longitude, Integer range) {
        String getEatDealQuery = "select D.id, D.name, E.restaurant_desc, E.menu_desc, E.notice, E.manual, E.refund_policy, E.question, E.price, E.discount_rate, E.menu_name, date_format(E.start_date, '%Y-%m-%d'), DATE_ADD(date_format(E.start_date, '%Y-%m-%d'), interval E.expired_date DAY), E.expired_date, E.emphasis,  E.id, D.latitude, D.longitude, D.address \n" +
                "from eat_deals as E\n" +
                "join (SELECT * FROM \n" +
                "\t\t(SELECT name, ( 6371 * acos( cos( radians(?) ) * cos( radians( latitude) ) * cos( radians( longitude ) - radians(?) ) + sin( radians(?) ) * sin( radians(latitude) ) ) ) AS distance, id, latitude, longitude, address \n" +
                "        FROM restaurants) DATA\n" +
                "      WHERE DATA.distance < ?\n" +
                "      ) as D\n" +
                " on E.restaurant_id = D.id\n" +
                " where E.status = 'ACTIVE' order by E.updated_at desc";

        List<GetEatDeal> getEatDeals = jdbcTemplate.query(getEatDealQuery,
                (rs, rowNum) -> new GetEatDeal(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getInt(9),
                        rs.getInt(10),
                        rs.getString(11),
                        rs.getString(12),
                        rs.getString(13),
                        rs.getInt(14),
                        rs.getString(15),
                        rs.getInt(16),
                        rs.getDouble(17),
                        rs.getDouble(18),
                        rs.getString(19)
                )
                ,latitude, longitude, latitude, range);

        for(GetEatDeal getEatDeal : getEatDeals) {
            String[] addressArr = getEatDeal.getAddress().split(" ");

            if(addressArr[0].equals("서울특별시")) {
                getEatDeal.setAddress(addressArr[2].substring(0, addressArr[2].length()-1));
            }else {
                getEatDeal.setAddress(addressArr[1].substring(0, addressArr[1].length()-1));
            }

            getEatDeal.setImgUrls(getEatDealUrl(getEatDeal.getEatDealId()));
        }
        return getEatDeals;
    }

    private List<String> getEatDealUrl(int eatDealId) {
        String eatDealUrlQuery = "select img_url from eat_deal_imgs where eat_deal_id = ? and status = 'ACTIVE'";
        return jdbcTemplate.query(eatDealUrlQuery, (rs, rowNum) -> rs.getString("img_url"), eatDealId);
    }

    public int checkRestaurant(Integer restaurantId) {
        String checkRestaurantQuery = "select exists (select * from restaurants where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkRestaurantQuery, int.class, restaurantId);
    }

    public int checkMenu(Integer menuId) {
        String checkMenuQuery = "select exists (select * from eat_deals where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkMenuQuery, int.class, menuId);
    }

    public int orderEatDeal(Integer userId, PostEatDealReq postEatDealReq) {
        String orderEatDealQuery = "insert into eat_deal_orders(user_id, eat_deal_id, restaurant_id, price, status, payment, isUse, is_privacy) " +
                "values(?,?,?,?,'ACTIVE',?, 0, 1)";
        int price = getMenuPrice(postEatDealReq.getEatDealId());
        Object[] params = new Object[]{userId, postEatDealReq.getEatDealId(), postEatDealReq.getRestaurantId(), price, postEatDealReq.getPayment()};

        int result =  jdbcTemplate.update(orderEatDealQuery, params);
        String lastIdQuery = "select id from eat_deal_orders order by id desc limit 1";
        return jdbcTemplate.queryForObject(lastIdQuery, int.class);
    }

    private int getMenuPrice(Integer eatDealId) {
        String getMenuPriceQuery = "select (price - (price / 100 * discount_rate)) as price from eat_deals where id = ? ";
        return jdbcTemplate.queryForObject(getMenuPriceQuery, int.class, eatDealId);
    }

    public List<GetEatDealOrderRes> getEatDealOrders(Integer userId) {
        String getEatDealOrdersQuery = "select O.id, O.user_id, O.restaurant_id, O.eat_deal_id, O.price, O.created_at, O.updated_at, O.isUse, R.address, O.payment " +
                "from eat_deal_orders as O " +
                "left join eat_deal_imgs as I " +
                "on O.eat_deal_id = I.eat_deal_id " +
                "left join restaurants as R " +
                "on R.id = O.restaurant_id " +
                "where O.user_id = ? and O.status = 'ACTIVE' order by O.created_at desc";
        List<GetEatDealOrderRes> getEatDealOrderRes = jdbcTemplate.query(getEatDealOrdersQuery,
                (rs, rowNum) -> new GetEatDealOrderRes(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("restaurant_id"),
                        rs.getInt("eat_deal_id"),
                        rs.getInt("price"),
                        rs.getString("created_at"),
                        rs.getString("updated_at"),
                        rs.getInt("isUse"),
                        rs.getString("address"),
                        rs.getString("payment")

                ), userId);

        for(GetEatDealOrderRes order : getEatDealOrderRes) {
            String[] addressArr = order.getAddress().split(" ");
            if(addressArr[0].equals("서울특별시")){
                order.setAddress(addressArr[2].substring(0, addressArr[2].length()-1));
            }else {
                order.setAddress(addressArr[1].substring(0, addressArr[1].length()-1));
            }
            order.setMenuName(getMenuName(order.getEatDealId()));
            order.setRestaurantName(getRestaurantName(order.getRestaurantId()));
            order.setImgUrls(getEatDealUrl(order.getEatDealId()));
        }
        return getEatDealOrderRes;
    }

    private String getRestaurantName(int restaurantId) {
        String getRestaurantNameQuery = "select name from restaurants where id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getRestaurantNameQuery, String.class, restaurantId);
    }

    private String getMenuName(int menuId) {
        String getMenuNameQuery = "select name from menus where id = ? and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(getMenuNameQuery, String.class, menuId);
    }
}
