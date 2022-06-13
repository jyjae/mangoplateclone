package com.example.demo.src.oauth.facebook;

import com.example.demo.config.BaseException;
import com.example.demo.src.oauth.facebook.model.FacebookUser;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OauthProvider {
    @Autowired
    private final OauthDao dao;
    @Autowired
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(OauthProvider.class);

    @Autowired
    public OauthProvider(OauthDao dao, JwtService jwtService) {
        this.dao = dao;
        this.jwtService = jwtService;
    }

    public PostLoginRes loginFacebook(FacebookUser userInfo) throws BaseException{
        try {
//            HashMap<String, String> userInfo = getFacebookUserInfo(accessToken);

            if (userInfo.getEmail() == null | userInfo.getSocialProvider() == null) throw new BaseException(OAUTH_FAIL_LOAD_FACEBOOK_USER_INFO);

            FacebookUser user = (dao.checkFacebookUser(userInfo.getEmail(), userInfo.getSocialProvider()) == 1) ?
                    dao.getFacebookUserId(userInfo.getEmail(), userInfo.getSocialProvider()) : dao.createFacebookUser(userInfo);
            if (user == null) throw new BaseException(OAUTH_FAIL_LOAD_DATABASE_USER);
            String jwt = jwtService.createJwt(user.getId());
            return new PostLoginRes(user.getId(), jwt);
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        } catch (Exception exception){
            System.out.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public HashMap<String, String> getFacebookUserInfo(String accessToken)  {
        System.out.println("param : " + accessToken);
        HashMap<String,String> userInfo = new HashMap<String, String>();

        String reqURL = "https://graph.facebook.com/v14.0/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("fields=id,name,email"); // TODO REST_API_KEY 입력
            sb.append("&access_token="+accessToken); // TODO 인가코드 받은 redirect_uri 입력
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            String userId = element.getAsJsonObject().get("id").getAsString();
            String userName = URLDecoder.decode(element.getAsJsonObject().get("name").getAsString(),"UTF-8");
            String userEmail = URLDecoder.decode(element.getAsJsonObject().get("email").getAsString(),"UTF-8");

            System.out.println("userId : " + userId);
            System.out.println("userId : " + userName);
            System.out.println("userId : " + userEmail);

            userInfo.put("userName" , userName);
            userInfo.put("userEmail" , userEmail);
            userInfo.put("socialProvider" , "FACEBOOK");
            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;

    }
//    public int checkReviewId(Integer reviewId) throws BaseException {
//        try{
//            return dao.checkReviewId(reviewId);
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public int checkCommentId(Integer commentId) throws BaseException {
//        try{
//            return dao.checkCommentId(commentId);
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    public int checkUserId(Integer parentUserId) throws BaseException {
//        try{
//            return dao.checkUserId(parentUserId);
//        }catch (Exception e) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
