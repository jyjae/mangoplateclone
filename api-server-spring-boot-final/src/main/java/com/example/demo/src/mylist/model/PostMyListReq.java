package com.example.demo.src.mylist.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostMyListReq {
    private String title;
    private String content;
    private int userId;
}
