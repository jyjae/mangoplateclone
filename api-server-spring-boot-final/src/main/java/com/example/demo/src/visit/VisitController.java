package com.example.demo.src.visit;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.visit.model.GetVisitByUserRes;
import com.example.demo.src.visit.model.GetVisitRes;
import com.example.demo.src.visit.model.PostVisitReq;
import com.example.demo.src.visit.model.PutVisitReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaBuilder;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/visits")
public class VisitController {
    private final VisitProvider provider;
    private final VisitService service;

    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(VisitController.class);


    @Autowired
    public VisitController(VisitProvider provider, VisitService service, JwtService jwtService) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }

    @GetMapping("/{restaurant_id}")
    @ResponseBody
    public BaseResponse<GetVisitRes> getVisit(@PathVariable("restaurant_id") Integer restaurantId,
                                              @RequestHeader(value = "X-ACCESS-TOKEN", required = false) String accessToken) {
        if(accessToken ==null) {
            return new BaseResponse<>(EMPTY_JWT);
        }
        if(restaurantId == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetVisitRes getVisitRes = provider.getVisit(restaurantId, userIdxByJwt);
            return new BaseResponse<>(getVisitRes);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PutMapping()
    @ResponseBody
    public BaseResponse<Integer> updateVisit(@RequestHeader(value = "X-ACCESS-TOKEN", required = false) String accessToken,
                                             @RequestBody PutVisitReq putVisitReq) {
        if(accessToken==null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(putVisitReq.getRestaurantId()== null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        if(putVisitReq.getVisitId() == null) {
            return new BaseResponse<>(VISITS_EMPTY_VISIT_ID);
        }

        try {
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            return new BaseResponse<>(service.updateVisit(putVisitReq, userIdxByJwt));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @GetMapping("/user/{user_id}")
    @ResponseBody
    public BaseResponse<GetVisitByUserRes> getVisitByUser(@RequestParam(value = "lat", required = false) Double latitude,
                                                          @RequestParam(value = "long",required = false) Double longitude,
                                                          @RequestParam(value = "food-category",defaultValue = "1,2,3,4,5,6,7,8") List<Integer> foodCategories,
                                                          @RequestParam(value = "sort", defaultValue = "updated_at") String sortOption,
                                                          @PathVariable(value = "user_id", required = false) Integer userId
                                                          ) {
        if(userId == null) {
           return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        if(sortOption.equals("distance")) {
            if(latitude == null || longitude == null) {
                return new BaseResponse<>(SORT_DISTANCE_NEED_LATITUDE_LOGITUDE);
            }
        }
        try {
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            GetVisitByUserRes getVisitByUserRes = null;

            if(latitude == null && longitude == null) {
                getVisitByUserRes = provider.getVisitByUser(userId, foodCategories, sortOption, userIdxByJwt);

            }else {
                getVisitByUserRes = provider.getVisitByUser(userId, foodCategories, sortOption, latitude, longitude, userIdxByJwt);
            }

            return new BaseResponse<>(getVisitByUserRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("/{restaurant_id}/{visit_id}")
    @ResponseBody
    public BaseResponse<Integer> deleteVisit(@PathVariable("restaurant_id") Integer restaurantId,
                                             @PathVariable("visit_id") Integer visitId) {
        if(restaurantId == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        if(visitId == null) {
            return new BaseResponse<>(VISITS_EMPTY_VISIT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            int result = service.deleteVisit(restaurantId, userIdxByJwt, visitId);
            return new BaseResponse<>(result);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("")
    @ResponseBody
    public BaseResponse<Integer> createVisit(@RequestBody PostVisitReq postVisitReq) {
        if(postVisitReq.getRestaurantId() == null) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdxByJwt == null){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            int visitId = service.createVisit(postVisitReq.getRestaurantId(), userIdxByJwt);
            return new BaseResponse<>(visitId);

        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}
