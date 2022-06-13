package com.example.demo.src.report.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostReport {
    private Integer reviewUserId;
    private Integer reviewId;
    private String email;
    private String reason;
}
