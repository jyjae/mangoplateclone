package com.example.demo.src.mylist.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PutMyListReq {
    private int myListId;
    private String title;
    private String content;
}
