package com.example.demo.src.like;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class LikeService {
    private final LikeProvider provider;
    private final LikeDao dao;

    final Logger logger = LoggerFactory.getLogger(LikeService.class);

    @Autowired
    public LikeService(LikeProvider provider, LikeDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer postLike(int userId, int reviewId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkReviewId(reviewId) == 0) {
            throw new BaseException(REVIEWS_NOT_EXISTS_REVIEW);
        }
        if(provider.checkLiked(userId,reviewId) == 1) {
            throw new BaseException(LIKES_ALREADY_LIKED_REVIEW);
        }
        try {
            if(provider.checkCanceledLike(userId, reviewId) == 0)
                return dao.createRelation(userId,reviewId);
            else
                return dao.postLike(userId,reviewId);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer cancelLike(int userId, int reviewId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkReviewId(reviewId) == 0) {
            throw new BaseException(REVIEWS_NOT_EXISTS_REVIEW);
        }
        if(provider.checkCanceledLike(userId,reviewId) == 1) {
            throw new BaseException(LIKES_ALREADY_CANCELED_LIKE);
        }
        try {
            return dao.cancelLike(userId,reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
