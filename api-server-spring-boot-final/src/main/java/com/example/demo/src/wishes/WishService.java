package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.src.restaurant.RestaurantDao;
import com.example.demo.src.wishes.model.PostWishRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class WishService {
    private final WishProvider provider;
    private final WishDao dao;

    final Logger logger = LoggerFactory.getLogger(com.example.demo.src.wishes.WishService.class);

    public WishService(WishProvider provider, WishDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public PostWishRes postWish(Integer restaurantId, Integer userId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(dao.checkRestaurantId(restaurantId) == 0){
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            //유저-레스토랑 관계의 wish는 하나의 row만 있으면 되므로, 특정 유저가 특정 식당에 대한 wish 데이터가 존재하면 status로 관리한다.
            int wishId = dao.findWishId(restaurantId,userId);
            if(wishId == 0){
                PostWishRes result = dao.postWish(restaurantId, userId);
                if(result.getResult() == 0){
                    logger.warn("[WishService] postWish fail, userId: {}, restaurantId: {}", userId, restaurantId);
                    throw new BaseException(WISHES_POST_FAIL);
                }
                return result;
            } else {
                if(dao.changeStatusToActive(wishId) == 0) throw new BaseException(WISHES_POST_FAIL);
                return new PostWishRes(1, wishId);
            }
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public PostWishRes deleteWish(Integer restaurantId,Integer userId) throws BaseException {
// 요청을 두번 보냈을때,
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            int wishId = dao.findWishId(restaurantId,userId);
            if(wishId == 0) throw new BaseException(WISHES_DELETE_FAIL);
            if(dao.deleteWish(restaurantId, userId) == 0) throw new BaseException(WISHES_DELETE_FAIL);
            return new PostWishRes(1, wishId);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer postMemo(Integer wishId, String memo, Integer userId) throws BaseException {
// 요청을 두번 보냈을때,
        if(provider.checkWishId(wishId) == 0) throw new BaseException(WISHES_NOT_EXISTS_WISH);
        if(provider.getUserIdFromWish(wishId) != userId) throw new BaseException(WISHES_NOT_ALLOWED_MEMO);
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            return dao.postMemo(wishId, memo);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer putMemo(Integer wishId, String memo, Integer userId) throws BaseException {
        if(provider.checkWishId(wishId) == 0) throw new BaseException(WISHES_NOT_EXISTS_WISH);
        if(provider.getUserIdFromWish(wishId) != userId) throw new BaseException(WISHES_NOT_ALLOWED_MEMO);
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            return dao.postMemo(wishId, memo);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteMemo(Integer wishId, String memo, Integer userId) throws BaseException {
// 요청을 두번 보냈을때,
        if(provider.checkWishId(wishId) == 0) throw new BaseException(WISHES_NOT_EXISTS_WISH);
        if(provider.getUserIdFromWish(wishId) != userId) throw new BaseException(WISHES_NOT_ALLOWED_MEMO);
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            return dao.postMemo(wishId, null);
        } catch (Exception e) {
            System.out.println(e.toString());
            System.out.println(e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}