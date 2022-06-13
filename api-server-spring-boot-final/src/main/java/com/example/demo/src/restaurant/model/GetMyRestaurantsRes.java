package com.example.demo.src.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMyRestaurantsRes {
    private Integer id;
    private String name;
    private String address;
    private String foodCategory;
    private String createdAt;
}
