package com.example.demo.src.wishes.model;

import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetWishRestaurantRes {
    @Getter
    private Integer id;
    private Integer restaurantId;
    private String name;
    private String regionName;
    private Integer view;
    @Getter
    @Setter
    private Double ratingsAvg;
    private Integer numReviews;
    @Getter
    @Setter
    private Integer isWishes;
    private String memo;
    private String imgUrl;


}
