package com.example.demo.src.wishes;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.wishes.model.GetWishRestaurantRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.demo.src.wishes.model.*;
import com.example.demo.utils.JwtService;

import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/wishes")
public class WishController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishProvider provider;
    private final WishService service;
    private final JwtService jwtService;

    @Autowired
    public WishController(WishProvider provider, WishService service, JwtService jwtService){
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }
    /**
     * 유저의 가고싶다 항목에 포함된 식당 조회
     * @param
     * @return 식당 정보가 들어있는 객체
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetWishRestaurantRes>> getWishRestaurants(@RequestParam(value = "user-id", required = false) Integer targetUserId) {
        // 나를 포함한 특정 유저의 가고싶다 항목을 조회하는 API
        if(targetUserId == null) return new BaseResponse<>(WISHES_EMPTY_TARGET_USER_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            System.out.println("targetUserId :"+ targetUserId);
            System.out.println("userId :"+ userId);
            List<GetWishRestaurantRes> getWishRestaurantRes = provider.getWishRestaurants(userId, targetUserId);
            return new BaseResponse<>(getWishRestaurantRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 가고싶다 조회
     * @param restaurantId
     * @return 성공여부
     */
    @ResponseBody
    @GetMapping("/{restaurant_id}")
    public BaseResponse<GetWishRes> getWish(@PathVariable(value = "restaurant_id") Integer restaurantId) {
        if(restaurantId == null)
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            GetWishRes result = provider.getWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 가고싶다 등록
     * @param restaurantId
     * @return
     */
    @ResponseBody
    @PostMapping("/{restaurant_id}")
    public BaseResponse<PostWishRes> postWish(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null)
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            PostWishRes result = service.postWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @DeleteMapping("/{restaurant_id}")
    public BaseResponse<PostWishRes> deleteWish(@PathVariable("restaurant_id") Integer restaurantId) {
        if(restaurantId == null)
            return new BaseResponse<>(RESTAURANTS_EMPTY_RESTAURANT_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            PostWishRes result = service.deleteWish(restaurantId, userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @PostMapping("/memo")
    public BaseResponse<Integer> postMemo(@RequestBody PostMemoReq memo) {
        if(memo.getWishId() == null) return new BaseResponse<>(WISHES_EMPTY_WISH_ID);
        if(memo.getMemo() == null) return new BaseResponse<>(WISHES_EMPTY_MEMO_CONTENT);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int result = service.postMemo(memo.getWishId(), memo.getMemo(), userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PutMapping("/memo")
    public BaseResponse<Integer> putMemo(@RequestBody PostMemoReq memo) {
        if(memo.getWishId() == null) return new BaseResponse<>(WISHES_EMPTY_WISH_ID);
        if(memo.getMemo() == null) return new BaseResponse<>(WISHES_EMPTY_MEMO_CONTENT);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int result = service.putMemo(memo.getWishId(), memo.getMemo(), userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    @ResponseBody
    @DeleteMapping("/memo")
    public BaseResponse<Integer> deleteMemo(@RequestBody PostMemoReq memo) {
        if(memo.getWishId() == null) return new BaseResponse<>(WISHES_EMPTY_WISH_ID);
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int result = service.deleteMemo(memo.getWishId(), memo.getMemo(), userId);
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}

