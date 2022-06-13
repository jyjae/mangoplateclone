package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetRestaurantInfo {
    private String name;
    private Integer viewCnt;
    private String foodCategory;
    private String ImgUrl;
    private Integer reviewCnt;
}
