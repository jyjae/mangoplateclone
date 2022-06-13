package com.example.demo.src.eatdeal.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetEatDealOrderRes {
    private int id;
    private int userId;
    private int restaurantId;
    private String restaurantName;
    private int eatDealId;
    private String menuName;
    private int price;
    private String createdAt;
    private String updatedAt;
    private List<String> imgUrls;
    private int isUse;
    private String address;
    private String payment;

    public GetEatDealOrderRes(int id, int userId, int restaurantId, int eatDealId, int price, String createdAt, String updatedAt, int isUse, String address, String payment) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.eatDealId = eatDealId;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isUse = isUse;
        this.address = address;
        this.payment = payment;
    }
}
