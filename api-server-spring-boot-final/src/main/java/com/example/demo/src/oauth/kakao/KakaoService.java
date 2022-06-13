package com.example.demo.src.oauth.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.src.oauth.kakao.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.example.demo.config.BaseResponseStatus.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class KakaoService {
    private final KakaoProvider provider;
    private final KakaoDao dao;

    private final RestTemplate restTemplate;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(KakaoService.class);

    @Autowired
    public KakaoService(KakaoProvider provider, KakaoDao dao, RestTemplate restTemplate, JwtService jwtService) {
        this.provider = provider;
        this.dao = dao;
        this.restTemplate = restTemplate;

        this.jwtService = jwtService;
    }

    public PostLoginRes login(String accessToken) throws BaseException {
        try {
            Map<String, Object> userInfo = provider.getUserInfo(accessToken);
            Map<String, Object> kakaoAccount = (LinkedHashMap) userInfo.get("kakao_account");

            int userIdx = loginCheck(kakaoAccount);

            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);

        } catch (Exception e) {
            throw new BaseException(KAKAO_LOGIN_FAIL);
        }

    }

    private int loginCheck(Map<String, Object> kakaoAccount) throws BaseException {
        int userIdx = 0;

        if(kakaoAccount == null) {
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        }
        String email = (String)kakaoAccount.get("email");
        String nickName = (String)((LinkedHashMap)kakaoAccount.get("profile")).get("nickname");
        String profileImg = (String)((LinkedHashMap)kakaoAccount.get("profile")).get("profile_image_url");


        if(email == null) {
            throw new BaseException(POST_USERS_EMPTY_EMAIL);
        }

        if(provider.checkUsers(email) == 0) {
            userIdx = dao.createUser(email, nickName, profileImg);
        }else {
            userIdx = provider.getUserId(email);
        }

        return userIdx;
    }

    public String getKakaoAccessTokenV2(String code) throws BaseException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        /*
        중요한 특징중에 하나는 restTemplate의 exchange 메서드의 세번째 파라미터 request에는 무조건 MultiValueMap을 사용해야 한다.
         */
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "d5c8504fb1fb9f66c318c4b227b4c6d2");
        params.add("redirect_uri","http://localhost:9000/oauth/kakao/callback");
        params.add("code", code);

        //HttpHeader와 HttpBody를 하나의 오브젝트로 담는다.
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // 실제 요청

        ResponseEntity<Map> response =restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                kakaoTokenRequest,
                Map.class
        );

        logger.info("kakao login api, access_token: {}", (String)response.getBody().get("access_token"));

        return (String)response.getBody().get("access_token");
    }

    public String logout(String accessToken) throws BaseException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<HttpHeaders> kakaoRequest = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://kapi.kakao.com/v1/user/logout",
                    kakaoRequest,
                    String.class
            );

            return response.getBody();
        }catch (Exception e) {
            throw new BaseException(KAKAO_LOGOUT_FAIL);
        }
    }

}
