package com.example.demo.src.like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkCanceledLike(Integer userId, Integer reviewId) {
        String checkCanceledLikeQuery = "select exists (select * from likes where user_id = ? and review_id = ? and status ='INACTIVE')";
        return jdbcTemplate.queryForObject(checkCanceledLikeQuery, int.class, userId, reviewId);
    }

    public int checkLiked(Integer userId, Integer reviewId) {
        String checkLikedQuery = "select exists (select * from likes where user_id = ? and review_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkLikedQuery, int.class, userId, reviewId);
    }

    public int checkReviewId(Integer reviewId) {
        String checkReviewIdQuery = "select exists (select * from reviews where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewIdQuery, int.class, reviewId);
    }

    public int createRelation(int userId, int reviewId) {
        String createRelationQuery = "insert into likes (review_id, status, created_at, updated_at, user_id) VALUES (?, DEFAULT, DEFAULT, DEFAULT, ?)";
        return jdbcTemplate.update(createRelationQuery, reviewId, userId);

    }

    public int postLike(int userId, int reviewId) {
        String followUserQuery = "update likes t SET t.status = 'ACTIVE' WHERE t.user_id = ? and t.review_id = ?";
        return jdbcTemplate.update(followUserQuery, userId, reviewId);
    }

    public int cancelLike(int userId, int reviewId) {
        String followUserQuery = "update likes t SET t.status = 'INACTIVE' WHERE t.user_id = ? and t.review_id = ?";
        return jdbcTemplate.update(followUserQuery, userId, reviewId);
    }

    public int checkUser(int userIdx) {
        String checkUserQuery = "select exists (select * from users where id =? and status = 'ACTIVE') ";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userIdx);
    }

}
