package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PutVisitReq {
    private Integer restaurantId;
    private Integer visitId;
    private String content;
}
