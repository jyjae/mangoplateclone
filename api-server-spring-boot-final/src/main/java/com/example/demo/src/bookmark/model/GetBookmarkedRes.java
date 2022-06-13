package com.example.demo.src.bookmark.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.SpringVersion;

@Getter
@Setter
@AllArgsConstructor
public class GetBookmarkedRes {
    private int id;
    private String title;
    private String content;
    private Long view;
    private String createdAt;
    private int isBookmarked;
    private String imgUrl;
}
