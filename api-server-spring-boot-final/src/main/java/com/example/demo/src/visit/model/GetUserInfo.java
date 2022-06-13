package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserInfo {
    private int id;
    private String Name;
    private String profileImgUrl;
    private int reviewCnt;
    private int followCnt;
}
