package com.example.demo.src.oauth.kakao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostLoginRes {
    private int userIdx;
    private String jwt;
}
