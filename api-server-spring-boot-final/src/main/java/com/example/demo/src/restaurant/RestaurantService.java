package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.src.restaurant.model.PutRestaurantReq;
import com.example.demo.src.restaurant.model.PostRestaurantReq;
import com.example.demo.src.restaurant.model.PostRestaurantRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class RestaurantService {
    private final RestaurantProvider provider;
    private final RestaurantDao dao;

    final Logger logger = LoggerFactory.getLogger(RestaurantService.class);

    public RestaurantService(RestaurantProvider provider, RestaurantDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public int increaseView(Integer restaurantId) throws BaseException {
        if(provider.checkRestaurantId(restaurantId) == 0 ) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            return dao.increaseView(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public PostRestaurantRes createRestaurant(PostRestaurantReq postRestaurantReq, Integer userId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            if(dao.findByNameAndAddress(postRestaurantReq).equals(1)){
                throw new BaseException(RESTAURANTS_EXISTS_RESTAURANT);
            }else {
                return dao.createRestaurant(postRestaurantReq, userId);
            }
        }  catch (BaseException e) {
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteRestaurant(Integer restaurantId, Integer userId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkRestaurantId(restaurantId) == 0)
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        if(provider.checkMyRestaurant(restaurantId,userId) == 0)
            throw new BaseException(RESTAURANTS_CANT_ACCESS_RESTAURANT);
        try {
            if(dao.deleteRestaurant(restaurantId) == 1){
                return 1;
            }else {
                throw new BaseException(DELETE_FAIL_RESTAURANT);
            }
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Integer updateRestaurant(Integer restaurantId, PutRestaurantReq putRestaurantReq, Integer userId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(dao.checkRestaurantId(restaurantId) == 0)
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        if(provider.checkMyRestaurant(restaurantId, userId) == 0)
            throw new BaseException(RESTAURANTS_CANT_ACCESS_RESTAURANT);
        try {
            if(putRestaurantReq.getName() != null) {
                int result = dao.updateRestaurantName(putRestaurantReq.getName(), userId);
                if(result == 0) throw new BaseException(UPDATE_FAIL_RESTAURANT);
            }
            if(putRestaurantReq.getAddress() != null) {
                int result = dao.updateRestaurantAddress(putRestaurantReq.getAddress(), putRestaurantReq.getLatitude(), putRestaurantReq.getLongitude(),userId);
                if(result == 0) throw new BaseException(UPDATE_FAIL_RESTAURANT);
            }
            if(putRestaurantReq.getFoodCategory() != null) {
                int result = dao.updateRestaurantFoodCategory(putRestaurantReq.getFoodCategory(), userId);
                if(result == 0) throw new BaseException(UPDATE_FAIL_RESTAURANT);
            }
            return 1;
        } catch (BaseException e) {
            e.printStackTrace();
            throw new BaseException(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
