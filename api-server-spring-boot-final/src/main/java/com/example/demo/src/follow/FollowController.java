package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/follows")
public class FollowController {
    private final FollowService service;
    private final FollowProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    public FollowController(FollowService service, FollowProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }
    @GetMapping("")
    @ResponseBody
    public BaseResponse<Integer> getFollowStatus(@RequestParam(value = "followee-id") Integer followeeId) throws BaseException {
        if(followeeId == null) {
            return new BaseResponse<>(FOLLOWS_EMPTY_FOLLOWEE_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = provider.checkFollowed(userId, followeeId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("")
    @ResponseBody
    public BaseResponse<Integer> followUser(@RequestParam(value = "followee-id") Integer followeeId) throws BaseException {
        if(followeeId == null) {
            return new BaseResponse<>(FOLLOWS_EMPTY_FOLLOWEE_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.followUser(userId, followeeId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("")
    @ResponseBody
    public BaseResponse<Integer> unFollowUser(@RequestParam(value = "followee-id") Integer followeeId) throws BaseException {
        if(followeeId == null) {
            return new BaseResponse<>(FOLLOWS_EMPTY_FOLLOWEE_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.unfollowUser(userId, followeeId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
