package com.example.demo.src.visit;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.visit.model.GetRestaurantInfo;
import com.example.demo.src.visit.model.GetVisitByUserRes;
import com.example.demo.src.visit.model.GetVisitRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VisitProvider {
    private final VisitDao dao;

    final Logger logger = LoggerFactory.getLogger(VisitProvider.class);

    @Autowired
    public VisitProvider(VisitDao dao) {
        this.dao = dao;
    }

    public int checkRestaurant(Integer restaurantId) throws BaseException {
        try {
            return dao.checkRestaurant(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetVisitRes getVisit(Integer restaurantId, Integer userIdxByJwt) throws BaseException {

        if(checkUser(userIdxByJwt) ==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(checkRestaurant(restaurantId )==0) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        try {
            GetVisitRes getVisit = new GetVisitRes(0,0);
            int getVisitRes = dao.getVisitCheck(restaurantId, userIdxByJwt);
            if(getVisitRes != 0) {
                getVisit = dao.GetVisitRes(restaurantId, userIdxByJwt);
            }
            return getVisit;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkTodayVisit(Integer restaurantId, Integer userIdxByJwt, String currentDate) throws BaseException {
        try{
            return dao.checkTodayVisit(restaurantId, userIdxByJwt, currentDate);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(Integer userIdxByJwt) throws BaseException {
        try {
            return dao.checkUser(userIdxByJwt);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkVisit(Integer restaurantId, Integer userId, Integer visitId) throws BaseException {
        try {
            return dao.checkVisit(restaurantId, userId, visitId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetVisitByUserRes getVisitByUser(Integer userId, List<Integer> foodCategories, String sortOption, Integer userIdxByJwt) throws BaseException {
        if(checkUser(userId) ==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            GetVisitByUserRes getVisitByUserRes = dao.getVisitByUser(userId, foodCategories, sortOption, userIdxByJwt);
            return getVisitByUserRes;
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetVisitByUserRes getVisitByUser(Integer userId, List<Integer> foodCategories, String sortOption, Double latitude, Double longitude, Integer userIdxByJwt) throws BaseException {
        if(checkUser(userId) ==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            GetVisitByUserRes getVisitByUserRes = dao.getVisitByUser(userId, foodCategories, sortOption, latitude, longitude, userIdxByJwt);
            return getVisitByUserRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
    }

