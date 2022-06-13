package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetFollowRes {
    private int userId;
    private String userName;
    private String isHolic;
    private int postCnt;
    private int followerCnt;
    private String profileImg;
}
