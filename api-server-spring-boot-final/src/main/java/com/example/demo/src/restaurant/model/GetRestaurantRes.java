package com.example.demo.src.restaurant.model;

import lombok.*;

import java.math.BigInteger;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRestaurantRes {
    @Getter
    private Long id;
    private String name;
    private String regionName;
    private String foodCategory;
    @Getter
    private Double latitude;
    @Getter
    private Double longitude;

    @Getter
    @Setter
    private Double ratingsAvg;
    private Integer numReviews;
    @Getter
    @Setter
    private Double distance;
    @Getter
    @Setter
    private Integer isWishes;
    private Integer isVisits;
    private Integer view;
    private String address;
    private String imgUrl;


    public GetRestaurantRes(Long id, String name, String regionName, String foodCategory, Double latitude, Double longitude,
                            Integer numReviews, Double ratingsAvg, Double distance, Integer isWishes, Integer isVisits, Integer view, String address, String imgUrl) {
        this.id = id;
        this.name = name;
        this.regionName = regionName;
        this.foodCategory = foodCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numReviews = numReviews;
        this.ratingsAvg = ratingsAvg;
        this.distance = distance;
        this.isWishes = isWishes;
        this.isVisits = isVisits;
        this.view = view;
        this.address = address;
        this.imgUrl = imgUrl;
    }
    public GetRestaurantRes(Long id, String name, String regionName, String foodCategory, Double latitude, Double longitude,
                            Integer numReviews, Double ratingsAvg, Integer isWishes, Integer isVisits, Integer view, String address,String imgUrl) {
        this.id = id;
        this.name = name;
        this.regionName = regionName;
        this.foodCategory = foodCategory;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numReviews = numReviews;
        this.ratingsAvg = ratingsAvg;
        this.isWishes = isWishes;
        this.isVisits = isVisits;
        this.view = view;
        this.address = address;
        this.imgUrl = imgUrl;
    }
}
