package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;

import com.example.demo.src.restaurant.model.GetMyRestaurantsRes;
import com.example.demo.src.restaurant.model.GetRestaurantRes;

import com.example.demo.src.restaurant.model.GetRestaurantDetailRes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class RestaurantProvider {
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantProvider.class);

    public RestaurantProvider(RestaurantDao dao) {
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<GetRestaurantRes> getRestaurant(Double latitude, Double longitude, String foodCategories, int range, String sortOption, Integer userId) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetRestaurantRes> getRestaurantRes = dao.getRestaurant(latitude, longitude, foodCategories, range, sortOptionToQuery(sortOption), userId);
            return getRestaurantRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public List<GetRestaurantRes> getRestaurant(List<Integer> regionCode, String foodCategories, String sortOption, Integer userId) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            List<GetRestaurantRes> getRestaurantRes = dao.getRestaurant(regionCode.toString().replace("[", "(").replace("]", ")"), foodCategories, sortOptionToQuery(sortOption), userId);
            return getRestaurantRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     * 식당 존재 여부 체크
     * @param restaurantId
     * @return
     * @throws BaseException
     */
    public GetRestaurantDetailRes getRestaurantDetail(Integer restaurantId) throws BaseException {
        if(checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.getRestaurantDetail(restaurantId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetMyRestaurantsRes> getMyRestaurants(Integer userId) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            return dao.getMyRestaurants(userId);
        } catch (Exception e) {
            e.printStackTrace();
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


    public int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMyRestaurant(Integer restaurantId, Integer userId) throws BaseException {
        try {
            return dao.checkMyRestaurant(restaurantId,userId);
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public String sortOptionToQuery(String sortOption){
        switch (sortOption){
            case "rating": return "ratingsAvg desc";
            case "review": return "numReviews desc";
            case "recommend": return "ratingsAvg desc, numReviews desc";
            case "distance": return "distance";
            default: return "ratingsAvg desc";
        }
    }
}
