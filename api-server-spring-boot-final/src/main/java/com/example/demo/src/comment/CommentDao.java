package com.example.demo.src.comment;

import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PutCommentReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CommentDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int checkReviewId(Integer reviewId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId);
    }

    public int checkCommentId(Integer commentId,  int userId) {
        String checkCommentQuery = "select exists (select * from review_comments where id = ? and user_id = ? and status ='ACTIVE')";
        return jdbcTemplate.queryForObject(checkCommentQuery, int.class, commentId, userId);
    }

    public int  checkUserId(Integer parentUserId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, parentUserId);
    }

    public Integer createComment(PostCommentReq postCommentReq, int userId) {
        String createCommentQuery = "insert into review_comments(review_id, comment, level, `order`, group_num, user_id, parent_user_id, status) " +
                    "values(?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";

        int level = postCommentReq.getCommentId() !=null ? 2:1;

        //순서를 구해야 함
        Integer order = getLevelOrder(postCommentReq.getReviewId(), level);

        Object[] createParams = new Object[] {postCommentReq.getReviewId(), postCommentReq.getComment(), level, order+1, postCommentReq.getReviewId(), userId,  postCommentReq.getParentUserId()};
        int result = jdbcTemplate.update(createCommentQuery, createParams);

        String lastId = "select id from review_comments order by id desc limit 1";
        return jdbcTemplate.queryForObject(lastId, int.class);
    }

    //first: id, second: order
    private Integer getLevelOrder(Integer reviewId, int i) {
        String getLevelOrderQuery = "select count(*) from review_comments where review_id = ? and level = ? and status = 'ACTIVE' order by id desc";
        return jdbcTemplate.queryForObject(getLevelOrderQuery,
                int.class
                , reviewId, i);
    }

    public int updateComment(PutCommentReq putCommentReq) {
        String updateCommentQuery = "update review_comments set comment = ? where id = ?";
        return jdbcTemplate.update(updateCommentQuery, putCommentReq.getComment(), putCommentReq.getCommentId());
    }

    public int deleteComment(int commentId) {
        String deleteCommentQuery = "update review_comments set status = 'INACTIVE' where id = ?";
        return jdbcTemplate.update(deleteCommentQuery, commentId);
    }
}
