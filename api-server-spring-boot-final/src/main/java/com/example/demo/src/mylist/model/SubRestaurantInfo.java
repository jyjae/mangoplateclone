package com.example.demo.src.mylist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubRestaurantInfo {
    private int restaurantId;
    private String restaurantStatus;
    private String imgUrl;
    private String restaurantName;
    private String address;
    private Double ratingsAvg;
    private int isWishes;
    private int isVisits;
    private int reviewId;
    private int reviewUserID;
    private String reviewUserProfileImg;
    private String reviewUserName;
    private String reviewContent;


}
