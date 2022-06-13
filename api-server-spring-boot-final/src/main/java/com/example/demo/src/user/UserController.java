package com.example.demo.src.user;

import com.example.demo.src.review.upload.FileStore;
import com.example.demo.src.review.upload.UploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final FileStore fileStore;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, FileStore fileStore){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.fileStore = fileStore;
    }

    /**
//     * 회원 조회 API
//     * [GET] /users
//     * 회원 번호 및 이메일 검색 조회 API
//     * [GET] /users? Email=
//     * @return BaseResponse<List<GetUserRes>>
//     */
//    //Query String
//    @ResponseBody
//    @GetMapping("/email") // (GET) 127.0.0.1:9000/app/users
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
//        try{
//            if(Email == null){
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // Get Users
//            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
//            return new BaseResponse<>(getUsersRes);
//        } catch(BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    // Path-variable
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser() throws BaseException {
        //jwt에서 idx 추출.
        Integer userIdxByJwt = jwtService.getUserIdx();
        //userIdx와 접근한 유저가 같은지 확인

        if(userIdxByJwt == null) {
            return new BaseResponse<>(USERS_EMPTY_USER_ID);
        }

        // Get Users
        try{
            GetUserRes getUserRes = userProvider.getUser(userIdxByJwt);
            return new BaseResponse<>(getUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postUserReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        try {
            if(userProvider.checkEmail(postUserReq.getEmail()) == 1){
                throw new BaseException(POST_USERS_EXISTS_EMAIL);
            }
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
        if(postUserReq.getPassword() == null ){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if(postUserReq.getUserName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_USER_NAME);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        if(postLoginReq.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //이메일 정규표현
        if(!isRegexEmail(postLoginReq.getEmail())){
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        if(postLoginReq.getPassword() == null ){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        try{
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     *
     * @param userIdx
     * @param user
     * @return
     * @throws IOException
     */
    @ResponseBody
    @PutMapping(value = "/{userIdx}")
    public BaseResponse<Integer> modifyUserName(@PathVariable("userIdx") int userIdx,
                                                @ModelAttribute PutUserReq user) throws IOException {
        if(user.getPhoneNumber() == null && user.getUserName() == null && user.getFile()==null) {
            return new BaseResponse<>(NOT_EXISTS_INPUT_CHANGES);
        }
        try {
            UploadFile storeImageFile = null;
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(user.getFile()!=null) {
                storeImageFile = fileStore.storeFile(user.getFile());
            }

            //같다면 유저네임 변경
            PutUser putUserReq = new PutUser(userIdx,user.getPhoneNumber(),user.getUserName(), storeImageFile);
            int result =  userService.modifyUserName(putUserReq);
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            exception.printStackTrace();
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
