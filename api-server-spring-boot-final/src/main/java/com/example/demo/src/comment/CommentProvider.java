package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentProvider {
    private final CommentDao dao;

    final Logger logger = LoggerFactory.getLogger(CommentProvider.class);

    @Autowired
    public CommentProvider(CommentDao dao) {
        this.dao = dao;
    }

    public int checkReviewId(Integer reviewId) throws BaseException {
        try{
            return dao.checkReviewId(reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCommentId(Integer commentId, int userId) throws BaseException {
        try{
            return dao.checkCommentId(commentId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUserId(Integer parentUserId) throws BaseException {
        try{
            return dao.checkUserId(parentUserId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUserId(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
