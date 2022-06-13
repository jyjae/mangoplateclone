package com.example.demo.src.eatdeal;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.eatdeal.model.GetEatDealOrderRes;
import com.example.demo.src.eatdeal.model.GetEatDealRes;
import com.example.demo.src.eatdeal.model.PostEatDealReq;
import com.example.demo.src.eatdeal.model.PostEatDealRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/eatdeals")
public class EatDealController {
    private final EatDealProvider provider;
    private final EatDealService service;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(EatDealController.class);

    @Autowired
    public EatDealController(EatDealProvider provider, EatDealService service, JwtService jwtService) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping("/order")
    @ResponseBody
    public BaseResponse<List<GetEatDealOrderRes>> getEatDealOrders(@RequestHeader(value = "X-ACCESS-TOKEN", required = false) String accessToken) {
        if(accessToken == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<GetEatDealOrderRes> getEatDealOrderRes = provider.getEatDealOrders(userId);
            return new BaseResponse<>(getEatDealOrderRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("")
    @ResponseBody
    public BaseResponse<List<GetEatDealRes>> getEatDeals(
            @RequestParam(value = "lat", required = false) Double latitude,
            @RequestParam(value = "long", required = false) Double longitude,
            @RequestParam(value= "range", defaultValue = "3") Integer range) {
        if(latitude == null) {
            return new BaseResponse<>(EAT_DEALS_EMPTY_LATITUDE);
        }
        if(longitude == null) {
            return new BaseResponse<>(EAT_DEALS_EMPTY_LONGITUDE);
        }
        try {
            List<GetEatDealRes> getEatDealRes = provider.getEatDeals(latitude, longitude, range);
            return new BaseResponse<>(getEatDealRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("/order/{user_id}")
    @ResponseBody
    public BaseResponse<PostEatDealRes> orderEatDeal(@PathVariable("user_id") Integer userId,
                                                     @RequestBody PostEatDealReq postEatDealReq) throws BaseException {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(postEatDealReq.getIsPrivacy() ==  null || postEatDealReq.getIsPrivacy() ==0) {
            return new BaseResponse<>(NO_AGREE_PRIVACY);
        }
        if(postEatDealReq.getRestaurantId() == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        if(postEatDealReq.getEatDealId() == null) {
            return new BaseResponse<>(MENUS_EMPTY_EAT_DEAL_ID);
        }
        if(postEatDealReq.getPayment()==null) {
            return new BaseResponse<>(EAT_DEALS_EMPTY_PAYMENT);
        }

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userId != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            PostEatDealRes postEatDealRes = service.orderEatDeal(userId, postEatDealReq);
            return new BaseResponse<>(postEatDealRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
