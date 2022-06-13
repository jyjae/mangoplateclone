package com.example.demo.src.review.model;

import com.example.demo.src.visit.model.GetUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewImageRes {
    private GetUserInfo getUserInfo;
    private List<GetReviewImage> getReviewImageList;
}
