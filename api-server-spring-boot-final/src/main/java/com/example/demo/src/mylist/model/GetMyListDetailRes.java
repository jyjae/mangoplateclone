package com.example.demo.src.mylist.model;

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
public class GetMyListDetailRes {
    private int mylistId;
    private String createdAt;
    private int view;
    private int bookmarkCount;
    private int restaurantsCount;

    private String title;
    private int userId;
    private String userName;
    private String profileImgUrl;
    private int numReviews;
    private int numFollowers;
    private String content;
    @Setter
    @Getter
    private List<SubRestaurantInfo> restaurants;

    public GetMyListDetailRes(int mylistId, String createdAt, int view,String title,int userId,String userName,String profileImgUrl,int numReviews,int numFollowers,String content){
        this.mylistId = mylistId;
        this.createdAt = createdAt;
        this.view = view;
        this.title = title;
        this.userId = userId;
        this.userName = userName;
        this.numReviews = numReviews;
        this.numFollowers = numFollowers;
        this.content = content;
    }
    public GetMyListDetailRes(int mylistId, String createdAt, int view, int bookmarkCount, int restaurantsCount, String title,int userId,String userName,String profileImgUrl,int numReviews,int numFollowers,String content){
        this.mylistId = mylistId;
        this.createdAt = createdAt;
        this.view = view;
        this.bookmarkCount = bookmarkCount;
        this.restaurantsCount = restaurantsCount;
        this.title = title;
        this.userId = userId;
        this.userName = userName;
        this.numReviews = numReviews;
        this.numFollowers = numFollowers;
        this.content = content;
    }
}
