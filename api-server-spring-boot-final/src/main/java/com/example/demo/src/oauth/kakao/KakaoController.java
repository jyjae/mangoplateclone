package com.example.demo.src.oauth.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.oauth.kakao.model.PostLoginRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/oauth/kakao")
public class KakaoController {
    private final KakaoProvider provider;
    private final KakaoService service;

    final Logger logger = LoggerFactory.getLogger(KakaoController.class);

    @Autowired
    public KakaoController(KakaoProvider provider, KakaoService service) {
        this.provider = provider;
        this.service = service;
    }

    @ResponseBody
    @GetMapping("/callback")
    public BaseResponse<PostLoginRes> kakaoCallback(@RequestParam String code) {

        try {
            String accessTokenV2 = service.getKakaoAccessTokenV2(code);

            PostLoginRes postLoginRes = service.login(accessTokenV2);
            return new BaseResponse<>(postLoginRes);

        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }

    @PostMapping("/login")
    @ResponseBody
    public BaseResponse<PostLoginRes> getUserInfo(@RequestHeader("ACCESS_TOKEN") String accessToken) {
        if(accessToken == null) {
            return new BaseResponse<>(EMPTY_JWT);
        }
        try {
            PostLoginRes postLoginRes = service.login(accessToken);
            return new BaseResponse<>(postLoginRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/logout")
    public BaseResponse<String> logout(@RequestHeader("ACCESS_TOKEN") String accessToken) {

        if(accessToken == null) {
            return new BaseResponse<>(EMPTY_JWT);
        }
        try {
            return new BaseResponse<>(service.logout(accessToken));
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
