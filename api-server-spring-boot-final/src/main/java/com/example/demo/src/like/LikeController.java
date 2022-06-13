package com.example.demo.src.like;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/likes")
public class LikeController {
    private final LikeService service;
    private final LikeProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    public LikeController(LikeService service, LikeProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }
    @GetMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<Integer> getLikeStatus(@PathVariable(value = "review_id") Integer reviewId) throws BaseException {
        if(reviewId == null) {
            return new BaseResponse<>(LIKES_EMPTY_REVIEW_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = provider.getLiked(userId, reviewId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<Integer> postLike(@PathVariable(value = "review_id") Integer reviewId) throws BaseException {
        if(reviewId == null) {
            return new BaseResponse<>(LIKES_EMPTY_REVIEW_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.postLike(userId, reviewId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<Integer> cancelLike(@PathVariable(value = "review_id") Integer reviewId) throws BaseException {
        if(reviewId == null) {
            return new BaseResponse<>(LIKES_EMPTY_REVIEW_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.cancelLike(userId, reviewId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
