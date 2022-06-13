package com.example.demo.src.bookmark;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.bookmark.model.BookmarkedStatus;
import com.example.demo.src.bookmark.model.GetBookmarkCountRes;
import com.example.demo.src.bookmark.model.GetBookmarkedRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import com.example.demo.utils.ValidationRegex;

import java.util.List;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final BookmarkService service;
    private final BookmarkProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(BookmarkController.class);

    @Autowired
    public BookmarkController(BookmarkService service, BookmarkProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }
    @GetMapping("/{user_id}")
    @ResponseBody
    public BaseResponse<BookmarkedStatus> getBookmarkCount(@PathVariable(value = "user_id") Integer userId) throws BaseException {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        try {
            List<GetBookmarkCountRes> getBookmarkCountRes = provider.getBookmarkCount(userId);
            BookmarkedStatus bookmarkedStatus = service.classifyContentsType(getBookmarkCountRes);
            return new BaseResponse<>(bookmarkedStatus);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @GetMapping("/{user_id}/{contents_type}")
    @ResponseBody
    public BaseResponse<List<GetBookmarkedRes>> getBookmarkedContents(@PathVariable(value = "user_id") Integer userId,
                                                                      @PathVariable(value = "contents_type") String contentsType) throws BaseException {
        if(userId == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }
        if(contentsType == null){
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_TYPE);
        }
        if(!contentsType.equals("top_lists") && !contentsType.equals("mylists")  && !contentsType.equals("mango_pick_stories")){
            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
        }
        try {
            List<GetBookmarkedRes> getBookmarkedRes = provider.getBookmarkedContents(userId, contentsType);
            return new BaseResponse<>(getBookmarkedRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
    @PostMapping("")
    @ResponseBody
    public BaseResponse<Integer> postBookmark(@RequestParam(value = "contents-type") String contentsType,
                                              @RequestParam(value = "contents-id") Integer contentsId) throws BaseException {
        if(contentsType == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_TYPE);
        }
        if(contentsId == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_ID);
        }
        if(!contentsType.equals("top_lists")  && !contentsType.equals("mylists") && !contentsType.equals("mango_pick_stories")){
            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.postBookmark(userId, contentsType, contentsId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            e.printStackTrace();
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("")
    @ResponseBody
    public BaseResponse<Integer> cancelBookmark(@RequestParam(value = "contents-type") String contentsType,
                                            @RequestParam(value = "contents-id") Integer contentsId) throws BaseException {
        if(contentsType == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_TYPE);
        }
        if(contentsId == null) {
            return new BaseResponse<>(BOOKMARKS_EMPTY_CONTENT_ID);
        }
        if(!contentsType.equals("top_lists")  && !contentsType.equals("mylists") && !contentsType.equals("mango_pick_stories")){
            return new BaseResponse<>(BOOKMARKS_CONTENT_TYPE_INVALID_FORM);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            Integer result = service.cancelBookmark(userId, contentsType, contentsId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }


}
