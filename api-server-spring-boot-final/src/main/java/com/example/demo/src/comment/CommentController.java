package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
import com.example.demo.src.comment.model.PutCommentReq;
import com.example.demo.src.comment.model.PutCommentRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService service;
    private final CommentProvider provider;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(CommentService service, CommentProvider provider, JwtService jwtService) {
        this.service = service;
        this.provider = provider;
        this.jwtService = jwtService;
    }

    @PutMapping("")
    @ResponseBody
    public BaseResponse<PutCommentRes> updateComment(@RequestBody PutCommentReq putCommentReq) {
        if(putCommentReq.getCommentId()==null) {
            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT_ID);
        }
        if(putCommentReq.getComment()==null) {
            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인

            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            PutCommentRes putCommentRes = service.updateComment(putCommentReq, userId);
            return new BaseResponse<>(putCommentRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @DeleteMapping("/{comment_id}")
    @ResponseBody
    public BaseResponse<Integer> deleteComment(@PathVariable("comment_id") Integer commentId) {
        if(commentId==null) {
            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT_ID);
        }
        try {
            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인

            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            return new BaseResponse<>(service.deleteComment(commentId, userId));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @PostMapping("")
    @ResponseBody
    public BaseResponse<PostCommentRes> createComment(@RequestBody PostCommentReq postCommentReq) throws BaseException {
        if(postCommentReq.getReviewId()==null) {
            return new BaseResponse<>(COMMENTS_EMPTY_REVIEW_ID);
        }
        if(postCommentReq.getComment()== null) {
            return new BaseResponse<>(COMMENTS_EMPTY_COMMENT);
        }
        if(postCommentReq.getCommentId()!= null) {
            if(postCommentReq.getParentUserId()== null) {
                return new BaseResponse<>(COMMENTS_EMPTY_PARENT_USER_ID);
            }
        }

        try {
            Integer userId = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인

            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            PostCommentRes postCommentRes = service.createComment(postCommentReq, userId);
            return new BaseResponse<>(postCommentRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

}
