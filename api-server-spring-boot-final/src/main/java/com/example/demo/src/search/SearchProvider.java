package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.GetSearchRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;

@Service
public class SearchProvider {
    private final SearchDao dao;

    final Logger logger = LoggerFactory.getLogger(SearchProvider.class);


    @Autowired
    public SearchProvider(SearchDao dao) {
        this.dao = dao;
    }

    public List<GetSearchRes> search(String search, Double latitude, Double longitude, Integer userId) throws BaseException {
        if(userId!=null) {
            if(checkUser(userId) == 0) {
                throw new BaseException(USERS_NOT_EXISTS_USER);
            }
        }
        try {
            if(longitude == null && latitude == null && userId == null) {
                return dao.search(search);
            }
            return dao.search(search, latitude, longitude, userId);
        }catch (Exception e){
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private int checkUser(Integer userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
