package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.GetSearchRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SearchProvider provider;
    private final JwtService jwtService;
    //private final SearchService service;

    final Logger logger = LoggerFactory.getLogger(SearchController.class);

    public SearchController(SearchProvider provider, JwtService jwtService) {
        this.provider = provider;
        this.jwtService = jwtService;
    }

    @GetMapping()
    @ResponseBody
    public BaseResponse<List<GetSearchRes>> search(@RequestParam String search,
                                                   @RequestParam(value = "lat", required = false) Double latitude,
                                                   @RequestParam(value = "long",required = false) Double longitude,
                                                   @RequestHeader(value ="X-ACCESS-TOKEN", required = false) String accessToken) {
        if(accessToken == null) {
            if(latitude != null || longitude != null) {
                return new BaseResponse<>(EMPTY_ACCESS_TOKEN_LATITUDE_LONGITUDE);
            }
        }

        if(search == null) {
            return new BaseResponse<>(SEARCH_EMPTY_KEYWORD);
        }
//        if(latitude == null) {
//            return new BaseResponse<>(SEARCH_EMPTY_LATITUDE);
//        }
//        if(longitude == null) {
//            return new BaseResponse<>(SEARCH_EMPTY_LONGITUDE);
//        }
        try {
            Integer userId = null;
            if(accessToken != null) {
                if(latitude == null) {
                    return new BaseResponse<>(SEARCH_EMPTY_LATITUDE);
                }
                if(longitude == null) {
                    return new BaseResponse<>(SEARCH_EMPTY_LONGITUDE);
                }
                 userId = jwtService.getUserIdx();
                if (userId == null) {
                    return new BaseResponse<>(USERS_EMPTY_USER_ID);
                }
            }
            List<GetSearchRes> getSearchRes = provider.search(search, latitude, longitude, userId);
            return new BaseResponse<>(getSearchRes);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
