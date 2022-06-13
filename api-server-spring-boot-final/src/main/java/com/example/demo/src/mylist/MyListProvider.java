package com.example.demo.src.mylist;

import com.example.demo.config.BaseException;
import com.example.demo.src.mylist.model.GetMyListDetailRes;
import com.example.demo.src.mylist.model.GetMyListRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class MyListProvider {
    private final MyListDao dao;

    final Logger logger = LoggerFactory.getLogger(MyListProvider.class);

    @Autowired
    public MyListProvider(MyListDao dao) {
        this.dao = dao;
    }

    public List<GetMyListRes> getMyList(Integer userId) throws BaseException {
        if(checkUser(userId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try{
            //다른 유저에 접근할 수 있지
            if(checkMyList(userId) == 0 ) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
            return dao.getMyList(userId);
        }catch (BaseException e) {
            System.out.println(e.toString());
            throw new BaseException(e.getStatus());
        }catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetMyListDetailRes getMyListDetail(Integer targetUserId, Integer myListId, Integer userId) throws BaseException {
        if(checkUser(targetUserId) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try{
            if(checkMyListId(myListId) == 0 ) throw new BaseException(MYLISTS_NOT_EXISTS_MYLIST);
            if(checkUserMyListId(myListId, targetUserId) == 0 ) throw new BaseException(MYLISTS_NOT_USERS_MYLIST);
            return dao.getMyListDetail(targetUserId,myListId, userId);
        }catch (BaseException e) {
            System.out.println(e.toString());
            throw new BaseException(e.getStatus());
        }catch (Exception e) {
            System.out.println(e.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

// 유저 아이디로 mylist를 가지고 있는지 검사
    public int checkMyList(Integer userId) throws BaseException {
        try{
            return dao.checkMyList(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    마이리스트가 활성화 되어있는 리스트인지 검사
    public int checkMyListId(Integer myListId) throws BaseException {
        try{
            return dao.checkMyListId(myListId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    //    마이리스트는 있지만 안에 아무것도 없을 경우 검사
    public int checkMyListEmpty(Integer myListId) throws BaseException {
        try{
            return dao.checkMyListEmpty(myListId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
// 유저의 마이리스트인지 검사, 다른 유저의 마이리스트에 추가를 할 수 없음.
    public int checkUserMyListId(Integer myListId, Integer userId) throws BaseException {
        try{
            return dao.checkUserMyListId(myListId,userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkDuplicated(Integer myListId, Integer restaurantId) throws BaseException {
        try{
            return dao.checkDuplicated(myListId, restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRestaurantId(int restaurantId) throws BaseException {
        try {
            return dao.checkRestaurantId(restaurantId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
