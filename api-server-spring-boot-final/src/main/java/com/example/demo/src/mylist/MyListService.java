package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.src.mylist.model.DeleteMyListReq;
import com.example.demo.src.mylist.model.PostMyListReq;
import com.example.demo.src.mylist.model.PostMyListRes;
import com.example.demo.src.mylist.model.PutMyListReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
@Service
public class MyListService {
    private final MyListProvider provider;
    private final MyListDao dao;

    final Logger logger = LoggerFactory.getLogger(MyListService.class);

    @Autowired
    public MyListService(MyListProvider provider, MyListDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    @Transactional(rollbackFor = Exception.class)
    public PostMyListRes createMyList(PostMyListReq postMyListReq, Integer userId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try {
            PostMyListRes postMyListRes = new PostMyListRes(dao.createMyList(postMyListReq, userId), 0);
            return postMyListRes;
        }catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public PostMyListRes insert2MyList(List<Integer> restaurantId, Integer myListId) throws BaseException {
        if(provider.checkMyListId(myListId) == 0) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
        for(Integer resId : restaurantId){
            if(provider.checkRestaurantId(resId) == 0)
                throw new BaseException(RESTAURANTS_NOT_EXISTS_RESTAURANT);
        }

        int count = 0;
        try {
            for(Integer resId : restaurantId){
                if(provider.checkDuplicated(myListId, resId) == 1) { count++;}
                else dao.insert2MyList(resId, myListId);
            }
            return new PostMyListRes(myListId, count);
        }catch (Exception e) {
            System.out.println(e.toString());

            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public Integer updateMyList(PutMyListReq putMyListReq, Integer userId ) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkUserMyListId(putMyListReq.getMyListId(), userId) == 0) throw new BaseException(MYLISTS_NOT_USERS_MYLIST);
        if(provider.checkMyListId(putMyListReq.getMyListId()) == 0) {
            throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
        }
        try{
            return dao.updateMyList(putMyListReq);
        }catch (Exception e) {
            System.out.println(e.toString());

            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public int deleteMyList(Integer myListId, Integer userId ) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkMyListId(myListId) == 0) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
        if(provider.checkUserMyListId(myListId, userId) == 0) throw new BaseException(MYLISTS_NOT_USERS_MYLIST);

        try{
            deleteAllRestaurants(myListId);
            return dao.deleteMyList(myListId);
        }catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAllRestaurants(Integer myListId) throws BaseException {
        try{
            int result = dao.deleteAllRestaurants(myListId);
            if(result < 1) throw new BaseException(MYLISTS_DELETE_FAIL);
        }catch (BaseException e) {
            System.out.println(e.toString());
            throw new BaseException(e.getStatus());
        }catch (Exception e) {
            System.out.println(e.toString());

            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = Exception.class)
    public int deleteSomeRestaurants(Integer myListId,Integer userId, List<Integer> restaurantId) throws BaseException {
        if(provider.checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkMyListId(myListId) == 0) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
        if(provider.checkUserMyListId(myListId, userId) == 0) throw new BaseException(MYLISTS_NOT_USERS_MYLIST);
        if(provider.checkMyListEmpty(myListId) == 0) throw new BaseException(MYLISTS_EMPTY_RESTAURANT_IN_MYLIST);

        try{
            int count = 0, result = 0;
            for(Integer resId : restaurantId){
                result = dao.deleteRestaurants(resId,myListId);
                if(result == 1) count++;
            }
            if(count != restaurantId.size()) throw new BaseException(MYLISTS_DELETE_FAIL);
            return 1;

        }catch (BaseException e) {
            System.out.println(e.getMessage());
            throw new BaseException(e.getStatus());
        }catch (Exception e) {
            System.out.println(e.toString());

            throw new BaseException(DATABASE_ERROR);
        }
    }
}
