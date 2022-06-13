package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDeal {

    private int restaurantId;
    private String restaurantName;
    private String restaurantDesc;
    private String menuDesc;
    private String notice;
    private String manual;
    private String refundPolicy;
    private String question;
    private int price;
    private int discountRate;
    private String menuName;
    private String startDate;
    private String endDate;
    private int expiredDate;
    private String emphasis;
    private List<String> imgUrls;
    private int eatDealId;
    private Double latitude;
    private Double longitude;
    private String address;

    public GetEatDeal(int restaurantId, String restaurantName, String restaurantDesc, String menuDesc, String notice, String manual, String refundPolicy, String question, int price, int discountRate, String menuName, String startDate, String endDate, int expiredDate, String emphasis, int eatDealId,Double latitude , Double longitude, String address) {
        this.eatDealId = eatDealId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.restaurantDesc = restaurantDesc;
        this.menuDesc = menuDesc;
        this.notice = notice;
        this.manual = manual;
        this.refundPolicy = refundPolicy;
        this.question = question;
        this.price = price;
        this.discountRate = discountRate;
        this.menuName = menuName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expiredDate = expiredDate;
        this.emphasis = emphasis;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
