package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetVisit {
    private Integer visitId;
    private Integer restaurantId;
    private String content;
    private GetRestaurantInfo getRestaurantInfo;
    private Integer likeCnt;
    private Integer commentCnt;
    private Integer isWish;
    private Integer isLike;
    private List<GetVisitComment> comments;

    public GetVisit(int visitId, int restaurantId, String content) {
        this.visitId = visitId;
        this.restaurantId = restaurantId;
        this.content = content;

    }
}
