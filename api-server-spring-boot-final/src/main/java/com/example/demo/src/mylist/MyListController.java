package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.mylist.model.*;
import com.example.demo.utils.JwtService;
import org.hibernate.sql.Insert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/mylists")
public class MyListController {
    private final MyListService service;
    private final MyListProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(MyListController.class);

    @Autowired
    public MyListController(MyListService service, MyListProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }

//    [DONE]특정 유저의 마이리스트 조회
    @GetMapping("/{user_id}")
    @ResponseBody
    public BaseResponse<List<GetMyListRes>> getMyList(@PathVariable(value = "user_id", required = false) Integer userId) {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
            List<GetMyListRes> getMyListRes = provider.getMyList(userId);
            return new BaseResponse<>(getMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
    // [DONE]특정 유저의 특정 마이리스트 조회
//    특정 유저가 가진 마이리스트 번호가 아니면 밸리데이션, 1,2,4번의 마이리스트를 가지고 있는 유저에게서 3번 마이리스트를 가져오면 안된다
    @GetMapping("/{user_id}/{mylist_id}")
    @ResponseBody
    public BaseResponse<GetMyListDetailRes> getMyListDetail(@PathVariable(value = "user_id", required = false) Integer targetUserId,
                                                            @PathVariable(value = "mylist_id", required = false) Integer myListId) {
        if(targetUserId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(myListId == null) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            GetMyListDetailRes getMyListDetailRes = provider.getMyListDetail(targetUserId, myListId, userId);
            return new BaseResponse<>(getMyListDetailRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

//    새로운 마이리스트 등록, 다른 사람의 마이리스트에는 접근 X, 식당을 바로 마이리스트에 추가할 수 있음. 새로운
    @PostMapping("")
    @ResponseBody
    public BaseResponse<PostMyListRes> createMyList(@RequestParam(value = "restaurant-id",required = false) Optional<List<Integer>> restaurantId,
                                                    @RequestBody PostMyListReq postMyListReq) {
        if(postMyListReq.getTitle().equals(null)) return new BaseResponse<>(MYLISTS_EMPTY_TITLE);
        PostMyListRes postMyListRes;
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(restaurantId.isEmpty())
                postMyListRes = service.createMyList(postMyListReq, userId);
            else
                postMyListRes = service.insert2MyList(restaurantId.get(),service.createMyList(postMyListReq, userId).getMyListId());
            return new BaseResponse<>(postMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    기존 마이리스트에 식당 추가 , 마이리스트가 없는데도 입력 가능.
    @PostMapping("/{mylist_id}")
    @ResponseBody
    public BaseResponse<PostMyListRes> insert2MyList(@PathVariable(value = "mylist_id", required = false) Integer myListId,
                                               @RequestParam(value = "restaurant-id",required = false) List<Integer> restaurantId) {
        if(myListId == null) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
        if(restaurantId.equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_RESTAURANT_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(provider.checkUser(userId) == 0) {
                return new BaseResponse<>(USERS_NOT_EXISTS_USER);
            }
            PostMyListRes postMyListRes = service.insert2MyList(restaurantId, myListId);
            return new BaseResponse<>(postMyListRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

//    마이리스트 삭제
    @DeleteMapping("/{mylist_id}")
    @ResponseBody
    public BaseResponse<Integer> deleteMyList(@PathVariable(value = "mylist_id", required = false) Integer myListId,
                                              @RequestParam(value = "restaurant-id", required = false) List<Integer> restaurantsId) {
        if(myListId == null) return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
//             1. 마이리스트 전체를 삭제하고싶을때
            if(restaurantsId == null )
                return new BaseResponse<>(service.deleteMyList(myListId, userId));
//             2. 마이리스트 안에 식당 목록에서 식당을 제거하고싶을때
            else
                return new BaseResponse<>(service.deleteSomeRestaurants(myListId, userId, restaurantsId));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
//    마이리스트 수정, 유저가 해당 마이리스트를 가졌는지 체크
    @PutMapping("")
    @ResponseBody
    public BaseResponse<Integer> updateMyList(@RequestBody PutMyListReq putMyListReq) {
        if(Optional.ofNullable(putMyListReq.getMyListId()).equals(null)) {
            return new BaseResponse<>(MYLISTS_EMPTY_MYLIST_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            return new BaseResponse<>(service.updateMyList(putMyListReq, userId));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
