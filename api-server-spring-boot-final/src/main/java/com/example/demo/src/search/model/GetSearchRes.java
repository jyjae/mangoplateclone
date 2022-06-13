package com.example.demo.src.search.model;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSearchRes {
    private Long id;
    private String name;
    private String address;
    private String foodCategory;
    private Double latitude;
    private Double longitude;
    private Integer numReviews;
    private Double ratingsAvg;
    private Integer isWishes;
    private Integer isVisits;
    private Integer view;
    private String imgUrl;
    private Double distance;

    public GetSearchRes(long id, int numReviews, String name, double longitude, double latitude, double longitude1, String imgUrl, double longitude2, String foodCategory, int view, int isWishes, String foodCategory1,Double distance) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.foodCategory = foodCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numReviews = numReviews;
        this.ratingsAvg = ratingsAvg;
        this.isWishes = isWishes;
        this.isVisits = isVisits;
        this.view = view;
        this.imgUrl = imgUrl;
    }

    public GetSearchRes(Long id, String name, String address, String foodCategory, Double latitude, Double longitude, Integer numReviews, Double ratingsAvg, Integer view, String imgUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.foodCategory = foodCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numReviews = numReviews;
        this.ratingsAvg = ratingsAvg;
        this.view = view;
        this.imgUrl = imgUrl;
    }
}
