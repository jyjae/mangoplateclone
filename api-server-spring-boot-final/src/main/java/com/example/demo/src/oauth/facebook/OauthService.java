package com.example.demo.src.oauth.facebook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OauthService {
    private final OauthProvider provider;
    private final OauthDao dao;

    final Logger logger = LoggerFactory.getLogger(OauthService.class);

    @Autowired
    public OauthService(OauthProvider provider, OauthDao dao) {
        this.provider = provider;
        this.dao = dao;
    }
//
//    @Transactional(rollbackFor = Exception.class)
//    public PostCommentRes createComment(PostCommentReq postCommentReq, int userId) throws BaseException {
//        if(provider.checkReviewId(postCommentReq.getReviewId()) == 0) {
//            throw new BaseException(COMMENTS_NOT_EXISTS_REVIEW);
//        }
//        // 댓글의 대댓글인 경우
//        if(postCommentReq.getCommentId()!=null) {
//            if(provider.checkCommentId(postCommentReq.getCommentId()) == 0) {
//                throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
//            }
//            if(provider.checkUserId(postCommentReq.getParentUserId()) == 0) {
//                throw new BaseException(COMMENTS_NOT_EXISTS_PARENT_USER_ID);
//            }
//        }
//
//        try {
//            PostCommentRes postCommentRes = new PostCommentRes(dao.createComment(postCommentReq, userId));
//            return postCommentRes;
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public PutCommentRes updateComment(PutCommentReq putCommentReq) throws BaseException {
//        if(provider.checkCommentId(putCommentReq.getCommentId()) == 0) {
//            throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
//        }
//        try{
//            PutCommentRes putCommentRes = new PutCommentRes(dao.updateComment(putCommentReq));
//            return putCommentRes;
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public int deleteComment(int commentId) throws BaseException {
//        if(provider.checkCommentId(commentId) == 0) {
//            throw new BaseException(COMMENTS_NOT_EXISTS_COMMENT);
//        }
//        try{
//            return dao.deleteComment(commentId);
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

//    curl -i -X GET \
// "https://graph.facebook.com/v14.0/me?fields=id%2Cname&access_token=EAAOZCaRibwzkBAI6SNanrurXOfrXP6fmD75SPKzDcYrwYi2bIY93jzyfwiuGzB8BBgiw6etOqsSWfSq7YMwwn78kZAXf5f5YmTX8k3IxKvQMO8KK65YRyyTCbEFeQt1HNchytJbyf38Hg8YbXRMLYoKrnRjZB8Upk1zq3MiCW0kCgBlx7rcmrNipBsgHgwwlPEn99W2TIdrnuQNTZAEGdO10awuMgdkiKWZA2KcaOQvRW2qBaZA3pT"
}
