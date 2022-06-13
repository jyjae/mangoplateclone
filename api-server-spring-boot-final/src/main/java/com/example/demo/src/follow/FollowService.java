package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FollowService {
    private final FollowProvider provider;
    private final FollowDao dao;

    final Logger logger = LoggerFactory.getLogger(FollowService.class);

    @Autowired
    public FollowService(FollowProvider provider, FollowDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer followUser(int userId, int followeeId) throws BaseException {
        if(followeeId == userId) {
            throw new BaseException(FOLLOWS_CANT_FOLLOW_SELF);
        }
        if(provider.checkUser(followeeId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkFollowed(userId,followeeId) == 1) {
            throw new BaseException(FOLLOWS_ALREADY_FOLLOWED_USER);
        }
        try {
            if(provider.checkUnFollowed(userId,followeeId) == 0)
                return dao.createRelation(userId,followeeId);
            else
                return dao.followUser(userId,followeeId);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer unfollowUser(int userId, int followeeId) throws BaseException {
        if(followeeId == userId) {
            throw new BaseException(FOLLOWS_CANT_UNFOLLOW_SELF);
        }
        if(provider.checkUser(followeeId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkUnFollowed(userId,followeeId) == 1) {
            throw new BaseException(FOLLOWS_ALREADY_UNFOLLOWED_USER);
        }

        try {
            return dao.unFollowUser(userId,followeeId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
