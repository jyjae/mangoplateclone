package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRestaurantRes {
    private Integer id;
    private String name;
    private String address;
    private String createdAt;
}
