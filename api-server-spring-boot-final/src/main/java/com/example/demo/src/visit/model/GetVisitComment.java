package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitComment {
    private Integer id;
    private Integer visitId;
    private Integer userId;
    private String userName;
    private String parentsUserName;
    private Integer isHolic;
    private String comment;
    private String updatedAt;
    private String profileImg;

}
