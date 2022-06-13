package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.src.eatdeal.model.PostEatDealReq;
import com.example.demo.src.eatdeal.model.PostEatDealRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class EatDealService {
    private final EatDealProvider provider;
    private final EatDealDao dao;

    final Logger logger = LoggerFactory.getLogger(EatDealService.class);

    @Autowired
    public EatDealService(EatDealProvider provider, EatDealDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public PostEatDealRes orderEatDeal(Integer userId, PostEatDealReq postEatDealReq) throws BaseException {
        if(provider.checkRestaurant(postEatDealReq.getRestaurantId()) == 0) {
            throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }
        if(provider.checkMenu(postEatDealReq.getEatDealId())==0) {
            throw new BaseException(EAT_DEALS_NOT_EXISTS);
        }
        try {
            PostEatDealRes postEatDealRes = new PostEatDealRes(dao.orderEatDeal(userId, postEatDealReq));
            return postEatDealRes;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
