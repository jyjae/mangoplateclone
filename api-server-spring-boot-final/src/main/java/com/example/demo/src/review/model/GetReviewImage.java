package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class GetReviewImage {
    private int imgId;
    private int reviewId;
    private String restaurantName;
    private String imgUrl;
    private int userId;
    private String userName;
    private String profileImgUrl;
    private String content;
    private int isLike;
    private String updatedAt;

    public GetReviewImage(int imgId, int reviewId, String restaurantName, String imgUrl, int userId, String userName, String profileImgUrl, String content, String updatedAt) {
        this.imgId = imgId;
        this.reviewId = reviewId;
        this.restaurantName = restaurantName;
        this.imgUrl = imgUrl;
        this.userId = userId;
        this.userName = userName;
        this.profileImgUrl = profileImgUrl;
        this.content = content;
        this.updatedAt = updatedAt;
    }
}
