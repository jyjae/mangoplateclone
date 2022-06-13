package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealRes {
    private int eatDealId;
    private int restaurantId;
    private String restaurantName;
    private String[] restaurantDesc;
    private String[] menuDesc;
    private String[] notice;
    private String[] manual;
    private String[] refundPolicy;
    private String[] question;
    private int price;
    private int discountRate;
    private String menuName;
    private String startDate;
    private String endDate;
    private Integer expiredDate;
    private String[] emphasis;
    private List<String> imgUrls;
    private Double latitude;
    private Double longitude;
    private String address;

    public GetEatDealRes(int eatDealId, int restaurantId, String restaurantName, int price, int discountRate, String startDate, String endDate, String menuName, Integer expiredDate, List<String> imgUrls,Double latitude, Double longitude, String address) {
        this.eatDealId = eatDealId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.price = price;
        this.discountRate = discountRate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.menuName = menuName;
        this.expiredDate = expiredDate;
        this.imgUrls = imgUrls;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
