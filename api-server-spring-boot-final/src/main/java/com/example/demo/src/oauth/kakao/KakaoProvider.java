package com.example.demo.src.oauth.kakao;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.Map;


@Service
public class KakaoProvider {
    private final KakaoDao dao;

    private final RestTemplate restTemplate;

    final Logger logger = LoggerFactory.getLogger(KakaoProvider.class);

    @Autowired
    public KakaoProvider(KakaoDao dao, RestTemplate restTemplate) {
        this.dao = dao;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getUserInfo(String accessToken) throws BaseException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<HttpHeaders> kakaoRequest = new HttpEntity<>(headers);

        ResponseEntity<Map> response = null;

        try {
            response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v2/user/me",
                    kakaoRequest,
                    Map.class
            );
        }catch (Exception e) {
            throw new BaseException(GET_KAKAO_USER_INFO_FALI);
        }

        return response.getBody();
    }


    public int checkUsers(String email) throws BaseException {
        try {
            return dao.checkUsers(email);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Integer getUserId(String email) throws BaseException {
        try {
            return dao.getUserId(email);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
