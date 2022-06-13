package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.src.eatdeal.model.GetEatDeal;
import com.example.demo.src.eatdeal.model.GetEatDealOrderRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class EatDealProvider {
    private final EatDealDao dao;

    final Logger logger = LoggerFactory.getLogger(EatDealProvider.class);

    @Autowired
    public EatDealProvider(EatDealDao dao) {
        this.dao = dao;
    }

    public List<GetEatDealRes> getEatDeals(Double latitude, Double longitude, Integer range) throws BaseException {
        try {
            List<GetEatDeal> getEatDealRes = dao.getEatDeals(latitude, longitude, range);
            return convertGetEatDealRes(getEatDealRes);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurant(Integer restaurantId) throws BaseException {
        try{
            return dao.checkRestaurant(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkMenu(Integer menuId) throws BaseException {
        try {
            return dao.checkMenu(menuId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetEatDealOrderRes> getEatDealOrders(Integer userId) throws BaseException {
        try {
            return dao.getEatDealOrders(userId);
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private List<GetEatDealRes> convertGetEatDealRes(List<GetEatDeal> getEatDeal) {
        List<GetEatDealRes> getEatDealRes = new ArrayList<>();

        for(GetEatDeal eatDeal : getEatDeal) {
            GetEatDealRes eatDealRes = new GetEatDealRes(eatDeal.getEatDealId(), eatDeal.getRestaurantId(), eatDeal.getRestaurantName(), eatDeal.getPrice(),
                    eatDeal.getDiscountRate(), eatDeal.getStartDate(), eatDeal.getEndDate(), eatDeal.getMenuName(), eatDeal.getExpiredDate(), eatDeal.getImgUrls(), eatDeal.getLatitude(), eatDeal.getLongitude(), eatDeal.getAddress());

            eatDealRes.setRestaurantDesc(eatDeal.getRestaurantDesc() != null ? eatDeal.getRestaurantDesc().split("\n") : null);
            eatDealRes.setMenuDesc(eatDeal.getMenuDesc() != null ? eatDeal.getMenuDesc().split("\n") : null);
            eatDealRes.setNotice(eatDeal.getNotice() != null ? eatDeal.getNotice().split("\n") : null);
            eatDealRes.setManual(eatDeal.getManual()!= null ? eatDeal.getManual().split("\n") : null);
            eatDealRes.setQuestion(eatDeal.getQuestion()!= null ? eatDeal.getQuestion().split("\n") : null);
            eatDealRes.setRefundPolicy(eatDeal.getRefundPolicy()!= null ? eatDeal.getRefundPolicy().split("\n") : null);
            eatDealRes.setEmphasis(eatDeal.getEmphasis()!= null ? eatDeal.getEmphasis().split("\n") : null);

            getEatDealRes.add(eatDealRes);
        }

        return getEatDealRes;
    }
}
