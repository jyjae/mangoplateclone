package com.example.demo.src.eatdeal.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostEatDealReq {
    private Integer restaurantId;
    private Integer eatDealId;
    private String payment;
    private Integer isPrivacy;
}
