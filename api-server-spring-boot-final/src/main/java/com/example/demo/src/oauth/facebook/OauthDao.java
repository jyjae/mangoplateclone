package com.example.demo.src.oauth.facebook;

import com.example.demo.src.oauth.facebook.model.FacebookUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class OauthDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkFacebookUser(String email, String socialProvider) {
        String checkFacebookUserQuery = "select exists (select * from users where email = ? and status = 'ACTIVE' and social_provider = ?)";
        return jdbcTemplate.queryForObject(checkFacebookUserQuery, int.class, email, socialProvider);

    }

    public FacebookUser getFacebookUserId(String email, String socialProvider) {
        String getFacebookUserIdQuery = "select id, email, user_name, social_provider from users where email = ? and status = 'ACTIVE' and social_provider = ?";
        Object[] params = new Object[]{email, socialProvider};
        return jdbcTemplate.queryForObject(getFacebookUserIdQuery,
                (rs,rowNum)-> new FacebookUser(
                        rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("user_name"),
                        rs.getString("social_provider")),
                params);
    }
//
    public FacebookUser createFacebookUser(FacebookUser userInfo) {
        System.out.println(userInfo.getEmail());
        System.out.println(userInfo.getUserName());
        System.out.println(userInfo.getSocialProvider());
        String checkCommentQuery = "INSERT INTO users (user_phone, user_name, profile_img_url, refresh_token, is_holic, status, created_at, updated_at, email, password, social_provider)\n" +
                "VALUES (null, ?, null, null, DEFAULT, DEFAULT, DEFAULT, DEFAULT, ?, null, ?)";
        Object[] params = new Object[]{userInfo.getUserName(), userInfo.getEmail(), userInfo.getSocialProvider()};
        this.jdbcTemplate.update(checkCommentQuery, params);

        String lastInserIdQuery = "select id, email, user_name, social_provider from users order by id desc limit 1";

        return this.jdbcTemplate.queryForObject(lastInserIdQuery,(rs,rowNum)-> new FacebookUser(
                rs.getInt("id"),
                rs.getString("email"),
                rs.getString("user_name"),
                rs.getString("social_provider")));
    }

//    public int checkUserId(Integer parentUserId) {
//        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
//        return jdbcTemplate.queryForObject(checkUserQuery, int.class, parentUserId);
//    }
//
//    public Integer createComment(PostCommentReq postCommentReq, int userId) {
//        String createCommentQuery = "insert into review_comments(review_id, comment, level, `order`, group_num, user_id, parent_user_id, status) " +
//                    "values(?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
//
//        int level = postCommentReq.getCommentId() !=null ? 2:1;
//
//        //순서를 구해야 함
//        Pair<Integer, Integer> pairs = getLevelOrder(postCommentReq.getReviewId(), level);
//
//        Object[] createParams = new Object[] {postCommentReq.getReviewId(), postCommentReq.getComment(), level, pairs.getSecond()+1, postCommentReq.getReviewId(), userId,  postCommentReq.getParentUserId()};
//        int result = jdbcTemplate.update(createCommentQuery, createParams);
//
//        return pairs.getFirst()+1;
//    }
//
//    //first: id, second: order
//    private Pair<Integer, Integer> getLevelOrder(Integer reviewId, int i) {
//        String getLevelOrderQuery = "select id,`order` from review_comments where review_id = ? and level = ? and status = 'ACTIVE' order by id desc limit 1";
//        return jdbcTemplate.queryForObject(getLevelOrderQuery,
//                (rs, rowNum) -> Pair.of(rs.getInt("id"), rs.getInt("order"))
//                , reviewId, i);
//    }
//
//    public int updateComment(PutCommentReq putCommentReq) {
//        String updateCommentQuery = "update review_comments set comment = ? where id = ?";
//        return jdbcTemplate.update(updateCommentQuery, putCommentReq.getComment(), putCommentReq.getCommentId());
//    }
//
//    public int deleteComment(int commentId) {
//        String deleteCommentQuery = "update review_comments set status = 'INACTIVE' where id = ?";
//        return jdbcTemplate.update(deleteCommentQuery, commentId);
//    }
}
