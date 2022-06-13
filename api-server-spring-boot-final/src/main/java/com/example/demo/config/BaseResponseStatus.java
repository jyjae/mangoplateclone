package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    SORT_DISTANCE_NEED_LATITUDE_LOGITUDE(false, 2011, "거리순 정렬은 위도경도 값이 필요합니다"),

    // [POST] /users
    POST_USERS_EMPTY_PASSWORD(false, 2014, "비밀번호를 입력해주세요"),
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EMPTY_USER_NAME(false, 2018, "유저 이름을 입력해주세요"),

    NOT_EXISTS_INPUT_CHANGES(false, 2019, "변경 사항이 없습니다."),

    GET_KAKAO_USER_INFO_FALI(false, 2020, "카카오 유저 정보 조회 실패했습니다."),
    KAKAO_LOGIN_FAIL(false, 2021, "카카오 로그인 실패"),
    KAKAO_LOGOUT_FAIL(false, 2022, "카카오 로그아웃 실패"),

    RESTAURANTS_EMPTY_RESTAURANT_ID(false, 2030, "식당 아이디 값을 확인해주세요"),
    MENUS_EMPTY_EAT_DEAL_ID(false, 2031, "잇딜 아이디 값을 확인해주세요"),


    NO_AGREE_PRIVACY(false, 3036, "정보동의를 하지 않았습니다."),
    EAT_DEALS_EMPTY_PAYMENT(false, 3035, "결제수단을 입력해주세요"),

    RESTAURANTS_EMPTY_USER_LOCATION_INFO(false, 2031, "사용자의 위도 경도 값을 입력해주세요"),
    RESTAURANTS_EMPTY_RESTAURANT_NAME(false, 2032, "식당 이름 값을 확인해주세요"),
    RESTAURANTS_EMPTY_RESTAURANT_ADDRESS(false, 2033, "식당 주소 값을 확인해주세요"),

    RESTAURANTS_EXISTS_RESTAURANT(false, 2034, "중복된 식당 정보입니다"),

    REVIEWS_EMPTY_REVIEW_ID(false, 2044, "리뷰 아이디 값을 확인해주세요"),

    RESTAURANTS_CANT_ACCESS_RESTAURANT(false, 2035, "식당 수정 및 삭제에 접근 가능한 권한이 없습니다"),

    RESTAURANTS_EMPTY_RESTAURANT_FOODCATEGORY(false, 2036, "음식 카테고리 값을 입력해주세요"),
    RESTAURANTS_EMPTY_UPDATE_DATA(false, 2037, "수정할 데이터 값을 입력해주세요"),
    RESTAURANTS_EMPTY_RESTAURANT_ADDRESS_INFO(false, 2038, "수정할 식당의 주소와 위치 정보 값을 입력해주세요"),
    //REVIEWS_EMPTY_REVIEW_ID(false, 2040, "리뷰 아이디 값을 확인해주세요"),

    REVIEWS_EMPTY_RESTAURANT_ID(false, 2041, "식당 아이디 값을 확인해주세요"),
    REVIEWS_EMPTY_SOCRE(false, 2042, "리뷰 점수를 입력해주세요"),
    REVIEWS_EMPTY_CONTENT(false, 2043, "리뷰 내용을 입력해주세요"),

    REVIEWS_EMPTY_IMG_ID(false, 2044, "리뷰 이미지 아이디를 입력해주세요."),

    COMMENTS_EMPTY_REVIEW_ID(false, 2060, "리뷰 아이디를 입력해주세요"),
    COMMENTS_EMPTY_COMMENT(false, 2061, "댓글 내용을 입력해주세요"),
    COMMENTS_EMPTY_PARENT_USER_ID(false, 2062, "부모 댓글의 유저 아이디를 입력해주세요"),
    COMMENTS_EMPTY_COMMENT_ID(false, 2063, "댓글 아이디를 입력해주세요"),
    REVIEWS_EMPTY_SCORE(false, 2064, "점수를 하나라도 입력해주세요"),

    OAUTH_FAIL_LOAD_FACEBOOK_USER_INFO(false,2081,"페이스북에서 유저 정보를 가져오는데 실패했습니다"),
    OAUTH_FAIL_LOAD_DATABASE_USER(false,2082,"DB에서 유저 정보를 가져오는데 실패했습니다"),
    EAT_DEALS_EMPTY_LATITUDE(false, 2070, "위도를 입력해주세요"),
    EAT_DEALS_EMPTY_LONGITUDE(false, 2071, "경도를 입력해주세요"),

    VISITS_EMPTY_VISIT_ID(false, 2080, "가봤어요 아이디를 입력해주세요"),


    FOLLOWS_EMPTY_FOLLOWEE_ID(false, 2090, "팔로우하려는 유저 아이디를 입력해주세요"),
    FOLLOWS_CANT_FOLLOW_SELF(false, 2091, "사용자 아이디와 팔로우하려는 아이디가 같습니다"),
    FOLLOWS_CANT_UNFOLLOW_SELF(false, 2092, "사용자 아이디와 언팔로우하려는 아이디가 같습니다"),
    FOLLOWS_ALREADY_FOLLOWED_USER(false, 2093, "이미 팔로우한 유저입니다"),
    FOLLOWS_ALREADY_UNFOLLOWED_USER(false, 2094, "이미 언팔로우한 유저입니다"),
    FOLLOWS_NOT_EXISTS_USER(false, 2095, "존재하지 않는 followee 아이디입니다"),

    MYLISTS_NOT_EXISTS_MYLIST(false, 2100, "마이리스트가 존재하지 않습니다"),
    MYLISTS_EMPTY_TITLE(false, 2101, "마이리스트의 제목을 입력해주세요"),
    MYLISTS_EMPTY_MYLIST_ID(false, 2102, "마이리스트 아이디를 입력해주세요"),
    MYLISTS_EMPTY_RESTAURANT_ID(false, 2103, "식당 아이디를 입력해주세요"),
    MYLISTS_NOT_USERS_MYLIST(false, 2104, "해당 유저의 마이리스트 항목이 아닙니다"),
    MYLISTS_EMPTY_RESTAURANT_IN_MYLIST(false, 2105, "마이리스트에 등록된 식당이 없습니다."),

    WISHES_EMPTY_TARGET_USER_ID(false, 2110, "가고싶다 항목을 조회할 유저 아이디를 입력해주세요"),
    WISHES_EMPTY_MEMO_CONTENT(false, 2111, "메모 내용을 입력해주세요"),
    WISHES_EMPTY_WISH_ID(false, 2112, "가고싶다 항목의 아이디를 입력해주세요"),

    LIKES_EMPTY_REVIEW_ID(false, 2120, "좋아요 요청할 리뷰 아이디를 입력해주세요"),
    LIKES_ALREADY_LIKED_REVIEW(false, 2121, "이미 좋아요 상태인 리뷰입니다"),
    LIKES_ALREADY_CANCELED_LIKE(false, 2122, "이미 좋아요 취소 상태인 리뷰입니다"),

    BOOKMARKS_EMPTY_CONTENT_TYPE(false, 2130, "북마크할 컨텐츠 유형을 입력해주세요"),
    BOOKMARKS_EMPTY_CONTENT_ID(false, 2131, "북마크할 컨텐츠 아이디를 입력해주세요"),
    BOOKMARKS_CONTENT_TYPE_INVALID_FORM(false, 2132, "잘못된 컨텐츠 유형 형식입니다"),

    REPORTS_EMPTY_USER_ID(false, 2140, "유저 아이디를 입력해주세요"),
    REPORTS_EMPTY_EMAIL(false, 2141, "이메일을 입력해주세요"),
    REPORTS_EMPTY_REASON(false, 2142, "신고 사유 입력해주세요"),
    USER_ID_SAME_REPORT_USER_ID(false, 2143, "신고하는 유저와 리뷰 작성 유저와 동일합니다"),

    SEARCH_EMPTY_KEYWORD(false, 2050, "조회 키워드를 입력해주세요"),
    SEARCH_EMPTY_LATITUDE(false, 2051, "위도를 입력해주세요"),
    SEARCH_EMPTY_LONGITUDE(false, 2052, "경도를 입력해주세요"),


    EMPTY_ACCESS_TOKEN_LATITUDE_LONGITUDE(false, 2053, "jwt 값이 없으면 위도, 경도 값도 없어야 합니다."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    USERS_NOT_EXISTS_USER(false, 3015, "존재하지 않은 유저입니다."),

    RESTAURANTS_NOT_EXISTS_RESTAURANT(false, 3030, "존재하지 않은 식당입니다."),
    RESTAURANTS_VIEW_INCREASE_FAIL(false, 3031, "조회수 증가 실패했습니다"),

    MENUS_NOT_EXISTS_MENU(false, 3032, "존재하지 않은 메뉴입니다."),
    EAT_DEALS_NOT_EXISTS(false, 3033, "존재하지 않은 잇딜입니다."),

    REVIEWS_NOT_EXISTS_REVIEW(false, 3040, "존재하지 않은 리뷰입니다."),
    REVIEWS_NOT_EXISTS_IMG(false, 3041, "존재하지 않은 이미지 입니다."),


    COMMENTS_NOT_EXISTS_REVIEW(false, 3060, "존재하지 않은 리뷰입니다"),
    COMMENTS_NOT_EXISTS_COMMENT(false, 3061, "존재하지 않은 댓글입니다."),
    COMMENTS_NOT_EXISTS_PARENT_USER_ID(false, 3062, "존재하지 않은 유저 입니다"),

    VISITS_NOT_EXISTS_VISIT(false, 3080, "존재하지 않은 가봤어요 입니다"),
    EXISTS_TODAY_VISIT(false, 3081, "같은 식당에 하루만 가봤어요 가능합니다"),

    WISHES_NOT_EXISTS_WISH(false, 3090, "존재하지 않은 가고싶다 항목입니다"),
    WISHES_NOT_ALLOWED_MEMO(false, 3091, "메모를 추가할 수 있는 권한이 없습니다"),
    WISHES_FAIL_GET_WISH(false, 3092, "가고싶다 항목을 불러올 수 없습니다."),
    WISHES_NOT_EXISTS_RESTAURANTS_IN_WISHES(false, 3093, "가고싶다 리스트에 식당 데이터가 없습니다"),

    BOOKMARKS_NOT_EXISTS_CONTENT(false, 3100, "존재하지 않은 컨텐츠입니다"),
    BOOKMARKS_ALREADY_BOOKMARKED(false, 3101, "이미 북마크한 컨텐츠입니다"),
    BOOKMARKS_ALREADY_UNMARKED(false, 3102, "이미 북마크 해제한 컨텐츠입니다"),
    REVIEWS_NOT_EXISTS_TODAY_REVIEW(false, 3063, "오늘 작성한 소식이 없습니다."),

    REPORTS_NOT_EXISTS_REVIEW_USERS(false, 3083, "리뷰 작성자가 존재하지 않습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),
    DELETE_FAIL_RESTAURANT(false,4015,"식당 삭제 실패"),
    UPDATE_FAIL_RESTAURANT(false,4016,"식당 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    WISHES_POST_FAIL(false, 4021, "가고싶다 등록에 실패하였습니다"),
    WISHES_DELETE_FAIL(false, 4022, "가고싶다 삭제에 실패하였습니다"),

    REVIEWS_CREATE_FAIL(false, 4030, "리뷰 생성에 실패하였습니다"),
    REVIEWS_UPDATE_FAIL(false, 4031, "리뷰 수정에 실패했습니다"),
    REVIEWS_DELETE_FAIL(false, 4032, "리뷰 삭제에 실패했습니다"),
    REVIEW_DELETE_IMG_FAIL(false, 4033, "리뷰 이미지 삭제에 실패했습니다."),
    MYLISTS_DELETE_FAIL(false, 4056, "마이리스트 삭제에 실패했습니다"),

    REPORT_CREATE_FAIL(false, 4070, "리뷰 신고 실패하셨습니다."),
    VISITS_MODIFY_FAIL(false, 4080, "가봤어요 수정이 실패했습니다.");



    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
