package com.example.demo.src.review.model;

import com.example.demo.src.comment.model.GetCommentRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetNewsRes {
    private Integer reviewId;
    private Integer userId;
    private String userName;
    private String profileImgUrl;
    private String content;
    private Integer score;
    private Integer restaurantId;
    private String restaurantName;
    private List<String> imgUrls;
    private List<GetCommentRes> comments;
    private Integer reviewCnt;
    private Integer followCnt;
    private int isHolic;
    private String updatedAt;
    private int wish;
    private int like;
    private int reviewLikeCnt;
    private int reviewCommentCnt;

    public GetNewsRes(Integer id, int userId, String userName, String content, int score, String profileImgUrl, int restaurantId, String restaurantName, int isHolic, String updatedAt) {
        this.reviewId = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.score = score;
        this.profileImgUrl = profileImgUrl;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.isHolic = isHolic;
        this.updatedAt = updatedAt;
    }


}
