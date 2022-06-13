package com.example.demo.src.restaurant;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.restaurant.model.*;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.RESTAURANTS_EMPTY_RESTAURANT_ID;


@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantProvider provider;
    private final RestaurantService service;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    public RestaurantController(RestaurantProvider provider, RestaurantService service, JwtService jwtService) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetRestaurantRes>> getRestaurant(@RequestParam(value = "lat", required = false) Double latitude,
                                                              @RequestParam(value = "long",required = false) Double longitude,
                                                              @RequestParam(value = "region-code", required = false) List<Integer> regionCode,
                                                              @RequestParam(value = "food-category",defaultValue = "1,2,3,4,5,6,7,8") List<Integer> foodCategories,
                                                              @RequestParam(value = "range", defaultValue = "3") Integer range,
                                                              @RequestParam(value = "sort", defaultValue = "rating") String sortOption) {
        logger.info("user lat -> ", latitude);
        logger.info("user long -> ", longitude);
//      food-category를 설정했는데 검색해보니 없는 경우엔 현재 [] 빈값을 리턴함
//      region-code에 대해서도 같은 결과가 있으므로 고쳐야돼.
//                food-category 형식상의 validation 필요.
//         region code가 null일때, 전체지역으로 생각해야함. 전체지역도 코드를 부여하자!
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            List<GetRestaurantRes> getRestaurantRes;

            // 위도와 경도가 들어오면 사용자가 위치 정보 사용을 동의했다고 가정.
            if (latitude != null && longitude != null) {
                getRestaurantRes = provider.getRestaurant(latitude, longitude,
                        foodCategories.toString().replace("[", "(").replace("]", ")"), range, sortOption, userId);

                return new BaseResponse<>(getRestaurantRes);
            }
            // 지역코드가 들어오면 사용자가 임의로 지역을 설정하여 검색
            else if (regionCode != null) {
                getRestaurantRes = provider.getRestaurant(regionCode, foodCategories.toString().replace("[", "(").replace("]", ")"),sortOption, userId);
                return new BaseResponse<>(getRestaurantRes);// 사용자의 위도 경도 정보가 없을 경우, 에러 발생
            }
            // 사용자의 위도 경도 정보와 지역 정보도 없을 경우, 에러 발생
            else {return new BaseResponse<>(RESTAURANTS_EMPTY_USER_LOCATION_INFO);}

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 식당 상세 정보 조회
     * @param restaurantId
     * @return
     */
    @ResponseBody
    @GetMapping("/{restaurant_id}")
    public BaseResponse<GetRestaurantDetailRes> getRestaurantDetail(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null ) {
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        }

        try{
            // 식당 상세 보기 API 호출 시 해당 식당의 조회수는 1 증가됨
            int result = service.increaseView(restaurantId);

            if(result == 0) {
                logger.warn("view increase fail, restaurantId: {}", restaurantId);
                return new BaseResponse<>(RESTAURANTS_VIEW_INCREASE_FAIL);
            }

            GetRestaurantDetailRes getReviewRes = provider.getRestaurantDetail(restaurantId);

            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 내가 등록한 식당 목록 조회
     * @param
     * @return
     */
    @ResponseBody
    @GetMapping("/my-restaurants")
    public BaseResponse<List<GetMyRestaurantsRes>> getMyRestaurants() {
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<GetMyRestaurantsRes> getMyRestaurantsRes = provider.getMyRestaurants(userId);

            return new BaseResponse<>(getMyRestaurantsRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
//    @ResponseBody
//    @GetMapping("/{user_id}")
//    public BaseResponse<GetRestaurantDetailRes> getMyRestaurant(@PathVariable("user_id") Integer userId) {
//        if(userId == null ) {
//            return new BaseResponse<>(USERS_EMPTY_USER_ID);
//        }
//
//        try{
//            GetRestaurantDetailRes getMyRes = provider.getMyRestaurant(userId);
//            return new BaseResponse<>(getMyRes);
//        }catch (BaseException e) {
//            return new BaseResponse<>(e.getStatus());
//        }
//
//    }

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostRestaurantRes> createRestaurant(@RequestBody PostRestaurantReq postRestaurantReq) {
        // 작성자 ID가 넘어오지 않았을 경우, jwt 토큰을 통해서 userId에 작성자 ID를 넣어준다
        if(postRestaurantReq.getName() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_NAME);
        }
        if(postRestaurantReq.getAddress() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ADDRESS);
        }
        if(postRestaurantReq.getLatitude() == null | postRestaurantReq.getLongitude() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_USER_LOCATION_INFO);
        }
        if(postRestaurantReq.getFoodCategory() == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_FOODCATEGORY);
        }
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            PostRestaurantRes postRestaurantRes = service.createRestaurant(postRestaurantReq, userId);
            return new BaseResponse<>(postRestaurantRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/{restaurant_id}")
    public BaseResponse<Integer> deleteRestaurant(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null)
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.deleteRestaurant(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PutMapping("/{restaurant_id}")
    public BaseResponse<Integer> updateRestaurant(@PathVariable("restaurant_id") Integer restaurantId, @RequestBody PutRestaurantReq putRestaurantReq) {
        if(restaurantId == null)
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        if(putRestaurantReq == null){
            return new BaseResponse<>(RESTAURANTS_EMPTY_UPDATE_DATA);
        }
        if(putRestaurantReq.getAddress() != null && (putRestaurantReq.getLatitude() == null || putRestaurantReq.getLongitude() == null)){
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ADDRESS_INFO);
        }
        if(putRestaurantReq.getAddress() == null && (putRestaurantReq.getLatitude() != null && putRestaurantReq.getLongitude() != null)){
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ADDRESS_INFO);
        }
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.updateRestaurant(restaurantId,putRestaurantReq, userId);
            return new BaseResponse<>(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
