package com.example.demo.src.mylist.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyListRes {
    private int id;
    private String title;
    private String content;
    private String imgUrl;
    private int bookmarkCount;

    public GetMyListRes(Integer id, String title, String content, String imgUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.bookmarkCount = 0;
    }
}
