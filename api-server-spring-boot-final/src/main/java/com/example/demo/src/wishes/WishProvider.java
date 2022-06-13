package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.src.wishes.model.GetWishRes;
import com.example.demo.src.wishes.model.GetWishRestaurantRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_NOT_EXISTS_RESTAURANT;

@Service
public class WishProvider {

    final Logger logger = LoggerFactory.getLogger(WishProvider.class);
    private final WishDao dao;

    @Autowired
    public WishProvider(WishDao dao) { this.dao = dao;}

    public GetWishRes getWish(int restaurantId, int userId) throws BaseException{
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            GetWishRes getWishRes = dao.getWish(restaurantId, userId);
//            if(getWishRes.getResult().equals(0)) throw new BaseException(WISHES_FAIL_GET_WISH);
            return getWishRes;
//        } catch (BaseException e) {
//            System.out.println(e.toString());
//            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetWishRestaurantRes> getWishRestaurants(Integer userId, Integer targetUserId) throws BaseException{
        if(checkUser(targetUserId) == 0 ) throw new BaseException(USERS_NOT_EXISTS_USER);
        if(checkExistsWishes(targetUserId) == 0 ) throw new BaseException(WISHES_NOT_EXISTS_RESTAURANTS_IN_WISHES);
        try {
            List<GetWishRestaurantRes> getWishRestaurantRes = dao.getWishRestaurants(userId,targetUserId);
            return getWishRestaurantRes;
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public Integer checkWishId(Integer wishId) throws BaseException{
        try {
            return dao.checkWishId(wishId);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public Integer checkExistsWishes(Integer userId) throws BaseException{
        try {
            return dao.checkExistsWishes(userId);
        } catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public Integer getUserIdFromWish(Integer wishId) throws BaseException{
        try {
            return dao.getUserIdFromWish(wishId);
        } catch (Exception e) {
            System.out.println(e.toString());
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

    public int checkRestaurantId(int restaurantId) throws BaseException {
        try {
            return dao.checkRestaurantId(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}





