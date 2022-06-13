package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;

import com.example.demo.src.review.upload.FileStore;
import com.example.demo.src.review.upload.UploadFile;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewProvider provider;
    private final ReviewService service;
    private final JwtService jwtService;

    private final FileStore fileStore;

    final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    public ReviewController(ReviewProvider provider, ReviewService service, JwtService jwtService, FileStore fileStore) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
        this.fileStore = fileStore;
    }

    @GetMapping("/today")
    @ResponseBody
    public BaseResponse<GetNewsRes> getTodayReview(@RequestHeader(value = "X-ACCESS-TOKEN", required = false) String jwt) {
        try{
            Integer userId = 0;

            if(jwt !=null){
                userId = jwtService.getUserIdx();
            }

            GetNewsRes getReviewTodayRes = provider.getReviewTodayRes(userId);

            if(getReviewTodayRes == null) {
                return new BaseResponse<>(REVIEWS_NOT_EXISTS_TODAY_REVIEW);
            }

            return new BaseResponse<>(getReviewTodayRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping()
    @ResponseBody
    public BaseResponse<List<GetNewsRes>> getNews(@RequestHeader(value = "X-ACCESS-TOKEN", required = false) String jwt,
                                                          @RequestParam(value = "score", required = false) List<Integer> scores) {
        if(scores == null||scores.isEmpty()) {
            return new BaseResponse<>(REVIEWS_EMPTY_SCORE);
        }
        try{
            Integer userId = 0;

            if(jwt !=null){
                userId = jwtService.getUserIdx();
            }

            List<GetNewsRes> getNewsRes = provider.getNews(userId, scores);


            return new BaseResponse<>(getNewsRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/holic")
    @ResponseBody
    public BaseResponse<List<GetNewsRes>> getNewsByHolic(@RequestParam(value = "score", required = false) List<Integer> scores) {
        if(scores == null||scores.isEmpty()) {
            return new BaseResponse<>(REVIEWS_EMPTY_SCORE);
        }
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<GetNewsRes> getHolicNews = provider.getHolicNews(userId, scores);
            return new BaseResponse<>(getHolicNews);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/follow")
    @ResponseBody
    public BaseResponse<List<GetNewsRes>> getNewsByFollow(@RequestParam(value = "score", required = false) List<Integer> scores) {
        if(scores == null||scores.isEmpty()) {
            return new BaseResponse<>(REVIEWS_EMPTY_SCORE);
        }
        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<GetNewsRes> getFollowNews = provider.getFollowNews(userId, scores);
            return new BaseResponse<>(getFollowNews);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/user/{user_id}")
    @ResponseBody
    public BaseResponse<List<GetReviewByUserRes>> getReviewByUser(@RequestParam(value = "lat", required = false) Double latitude,
                                                            @RequestParam(value = "long",required = false) Double longitude,
                                                            @RequestParam(value = "food-category",defaultValue = "1,2,3,4,5,6,7,8") List<Integer> foodCategories,
                                                            @RequestParam(value = "sort", defaultValue = "updated_at") String sortOption,
                                                            @RequestParam(value = "score", defaultValue = "1,3,5") List<Integer> scores,
                                                                  @PathVariable("user_id") Integer userId) {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        if(sortOption.equals("distance")) {
            if(latitude == null || longitude == null) {
                return new BaseResponse<>(SORT_DISTANCE_NEED_LATITUDE_LOGITUDE);
            }
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            List<GetReviewByUserRes> getReviewRes = null;
            if(latitude == null && longitude == null) {
                getReviewRes = provider.getReviewByUser(userId, foodCategories, sortOption, scores, userIdxByJwt);

            }else {
                getReviewRes = provider.getReviewByUser(userId, foodCategories, sortOption, latitude, longitude, scores, userIdxByJwt);

            }

            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/images/{user_id}")
    @ResponseBody
    public BaseResponse<GetReviewImageRes> getReviewImgUrl(@RequestParam(value = "lat", required = false) Double latitude,
                                                           @RequestParam(value = "long",required = false) Double longitude,
                                                           @RequestParam(value = "food-category",defaultValue = "1,2,3,4,5,6,7,8") List<Integer> foodCategories,
                                                           @RequestParam(value = "sort", defaultValue = "updated_at") String sortOption,
                                                           @PathVariable("user_id") Integer userId) {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try{
            Integer userIdxByJwt = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            GetReviewImageRes getReviewRes =  null;
            if(latitude == null && longitude == null) {
                getReviewRes = provider.getReviewImages(userId, foodCategories, sortOption, userIdxByJwt);
            }else {
                getReviewRes = provider.getReviewImages(userId, foodCategories, sortOption, userIdxByJwt, latitude, longitude);
            }
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("/images/{img_id}")
    @ResponseBody
    public BaseResponse<Integer> deleteReviewImg(@PathVariable("img_id") Integer imgId) {
        if(imgId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_IMG_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.deleteReviewImg(userId, imgId);
            return new BaseResponse<>(result);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 리뷰 작성
     * @param restaurantId
     * @param postReviewReq
     * @return
     */
    @PostMapping(value = "/{restaurant_id}", consumes = "multipart/form-data" )
    @ResponseBody
    public BaseResponse<PostReviewRes> createReview(@PathVariable("restaurant_id") Integer restaurantId,
                                                    @ModelAttribute PostReviewReq postReviewReq,
                                                    @RequestHeader(value="X-ACCESS-TOKEN", required = false) String accessToken) throws IOException {
        logger.info("[ReviewController] createReview, restaurantId: {}, postReviewReq: {}", restaurantId, postReviewReq.toString());
        if(accessToken == null) {
            return new BaseResponse<>(EMPTY_JWT);
        }
        if(restaurantId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_RESTAURANT_ID);
        }
        if(postReviewReq.getScore() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_SOCRE);
        }
        if(postReviewReq.getContent() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENT);
        }

        List<UploadFile> storeImageFiles=null;


        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(postReviewReq.getFile()!=null) {
                storeImageFiles = fileStore.storeFiles(postReviewReq.getFile());
            }

            Review review = new Review(postReviewReq.getContent(), postReviewReq.getScore(), storeImageFiles);
            PostReviewRes postReviewRes = new PostReviewRes(service.createReview(restaurantId, userId, review));
            logger.info("[ReviewController] createReview, userId: {}, reviewId: {}", userId, postReviewRes.getId());
            return new BaseResponse<>(postReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 리뷰 수정
     * @param reviewId
     * @param putReviewReq
     * @return
     * @throws IOException
     */
    @PutMapping(value = "/{review_id}", consumes = "multipart/form-data" )
    @ResponseBody
    public BaseResponse<PutReviewRes> updateReview(@PathVariable("review_id") Integer reviewId,
                                                   @ModelAttribute PutReviewReq putReviewReq,
                                                   @RequestHeader(value="X-ACCESS-TOKEN", required = false) String accessToken) throws BaseException, IOException {

        if(accessToken == null) {
            return new BaseResponse<>(EMPTY_JWT);
        }

        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        if(putReviewReq.getScore() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_SOCRE);
        }
        if(putReviewReq.getContent() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_CONTENT);
        }

        List<UploadFile> storeImageFiles = null;

        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            if(putReviewReq.getFile()!=null) {
                storeImageFiles = fileStore.storeFiles(putReviewReq.getFile());
            }

            Review review = new Review(putReviewReq.getContent(), putReviewReq.getScore(), storeImageFiles);
            PutReviewRes putReviewRes = service.updateReview(reviewId, userId, review);
            return new BaseResponse<>(putReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 리뷰 상세 보기
     * @param reviewId
     * @return
     */
    @GetMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<GetReviewRes> getReviewDetail(@PathVariable("review_id") Integer reviewId) {
        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        try {
            GetReviewRes getReviewRes = provider.getReviewDetail(reviewId);
            return new BaseResponse<>(getReviewRes);
        }catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }
    }

    @DeleteMapping("/{review_id}")
    @ResponseBody
    public BaseResponse<DeleteReviewRes> deleteReview(@PathVariable("review_id") Integer reviewId) throws BaseException {
        Integer userId = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인

        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        if(reviewId == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        try {
            DeleteReviewRes deleteReviewRes = service.deleteReview(reviewId, userId);
            return new BaseResponse<>(deleteReviewRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }



}
