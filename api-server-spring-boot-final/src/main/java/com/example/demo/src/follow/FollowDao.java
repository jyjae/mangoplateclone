package com.example.demo.src.follow;

import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PutCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FollowDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkUnFollowed(Integer userId, Integer followeeId) {
        String checkReviewQuery = "select exists (select * from follows where user_id = ? and follower_id = ? and status ='INACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class,  followeeId, userId);
    }

    public int checkFollowed(Integer userId, Integer followeeId) {
        String checkCommentQuery = "select exists (select * from follows where user_id = ? and follower_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkCommentQuery, int.class, followeeId, userId);
    }

    public int  checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public int createRelation(int userId, int followeeId) {
        String createRelationQuery = "insert into follows (user_id, follower_id, status, created_at, updated_at) VALUES (?, ?, 'ACTIVE', DEFAULT, DEFAULT)";
        return jdbcTemplate.update(createRelationQuery,followeeId, userId);

    }

    public int followUser(int userId, int followeeId) {
        String followUserQuery = "update follows t SET t.status = 'ACTIVE' WHERE t.user_id = ? and t.follower_id = ?";
        return jdbcTemplate.update(followUserQuery, followeeId, userId);
    }

    public int unFollowUser(int userId, int followeeId) {
        String followUserQuery = "update follows t SET t.status = 'INACTIVE' WHERE t.user_id = ? and t.follower_id = ?";
        return jdbcTemplate.update(followUserQuery, followeeId, userId);
    }
}
