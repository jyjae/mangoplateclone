package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.review.model.GetNewsRes;
import com.example.demo.src.review.model.GetReviewImageRes;
import com.example.demo.src.review.model.GetReviewRes;
import com.example.demo.src.review.model.GetReviewByUserRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReviewProvider {
    private final ReviewDao dao;

    final Logger logger = LoggerFactory.getLogger(ReviewProvider.class);

    @Autowired
    public ReviewProvider(ReviewDao dao) {
        this.dao = dao;
    }


    public GetReviewRes getReviewDetail(int reviewId) throws BaseException {
        if(checkReviewId(reviewId) == 0) {
            throw new BaseException(REVIEWS_NOT_EXISTS_REVIEW);
        }
        try {
            GetReviewRes getReviewRes = dao.getReviewDetail(reviewId);
            return getReviewRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    private int checkReviewId(int reviewId) throws BaseException {
        try{
            return dao.checkReviewId(reviewId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewUserId(int reviewId, Integer userId) throws BaseException {
        try{
            return dao.checkReviewAndUserId(reviewId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewId(int reviewId,  int userId) throws BaseException {
        try{
            return dao.checkReviewId(reviewId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurantId(int restaurantId) throws BaseException {
        try {
            return dao.checkRestaurantId(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(int userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewByUserRes> getReviewByUser(Integer userId, List<Integer> foodCategories, String sortOption, List<Integer> scores, Integer userIdxByJwt) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetReviewByUserRes> getReviewRes = dao.getReviewByUser(userId, foodCategories, sortOption, scores, userIdxByJwt);
            return getReviewRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetReviewByUserRes> getReviewByUser(Integer userId, List<Integer> foodCategories, String sortOption, Double latitude, Double longitude, List<Integer> scores, Integer userIdxByJwt) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetReviewByUserRes> getReviewRes = dao.getReviewByUser(userId, foodCategories, sortOption, latitude, longitude, scores, userIdxByJwt);
            return getReviewRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetReviewImageRes getReviewImages(Integer userId, List<Integer> foodCategories, String sortOption, Integer userIdxByJwt, Double latitude, Double longitude) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(checkUser(userIdxByJwt) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            GetReviewImageRes getReviewImageRes = dao.getReviewImages(userId, foodCategories, sortOption, userIdxByJwt, latitude, longitude);
            return getReviewImageRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetReviewImageRes getReviewImages(Integer userId, List<Integer> foodCategories, String sortOption, Integer userIdxByJwt) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(checkUser(userIdxByJwt) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            GetReviewImageRes getReviewImageRes = dao.getReviewImages(userId, foodCategories, sortOption, userIdxByJwt);
            return getReviewImageRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReviewImg(Integer imgId, Integer userId) throws BaseException {
        try {
            return dao.checkReviewImg(imgId, userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetNewsRes getReviewTodayRes(Integer userId) throws BaseException {
        if(userId != 0) {
            if (checkUser(userId) == 0) {
                throw new BaseException(USERS_NOT_EXISTS_USER);
            }
        }
        try {
            GetNewsRes getReviewTodayRes = dao.getReviewToday(userId);
            return getReviewTodayRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetNewsRes> getNews(Integer userId, List<Integer> scores) throws BaseException {
        if(userId != 0) {
            if (checkUser(userId) == 0) {
                throw new BaseException(USERS_NOT_EXISTS_USER);
            }
        }
        try {
            List<GetNewsRes> getNewsRes = dao.getNews(userId, scores);
            return getNewsRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetNewsRes> getHolicNews(Integer userId, List<Integer> scores) throws BaseException {
        if (checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetNewsRes> getHolicNews = dao.getHolicNews(userId, scores);
            return getHolicNews;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetNewsRes> getFollowNews(Integer userId, List<Integer> scores) throws BaseException {
        if (checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetNewsRes> getFollowNews = dao.getFollowNews(userId, scores);
            return getFollowNews;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }



}
