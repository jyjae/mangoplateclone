package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import com.example.demo.utils.SHA256;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }

        String pwd;
        try{
            //암호화
            pwd = new SHA256().encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);

        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            return new PostUserRes(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public int modifyUserName(PutUser putUserReq) throws BaseException {
        if(userProvider.checkUser(putUserReq.getUserIdx()) == 0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        try{
            int result = 0;
            if(putUserReq.getFileUrl()!= null) {
                result = userDao.modifyProfile(putUserReq.getUserIdx(), putUserReq.getFileUrl());
            }
            if(putUserReq.getUserName() != null) {
                result = userDao.modifyUserName(putUserReq.getUserIdx(), putUserReq.getUserName());
            }
            if(putUserReq.getPhoneNumber()!= null) {
                result = userDao.modifyPhoneNumber(putUserReq.getUserIdx(), putUserReq.getPhoneNumber());
            }

            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }

            return result;
        } catch(Exception exception){
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }





}
