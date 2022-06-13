package com.example.demo.src.visit.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetVisitByUserRes {
    private GetUserInfo getUserInfo;
    private List<GetVisit> getVisitList;


}
