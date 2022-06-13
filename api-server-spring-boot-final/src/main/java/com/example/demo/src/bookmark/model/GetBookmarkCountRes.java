package com.example.demo.src.bookmark.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBookmarkCountRes {
    private String contentsType;
    private Integer Count;
    private String ImgUrl;
}
