package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String userName;
    private String email;
    private String phoneNumber;
    private String profileImg;
    private List<GetFollowRes> followings;
    private List<GetFollowRes> followers;

    public GetUserRes(int userIdx, String userName, String email, String phoneNumber, String profileImg) {
        this.userIdx = userIdx;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImg = profileImg;
    }
}
