package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private Integer id;
    private Integer userId;
    private String userName;
    private String content;
    private int order;
    private List<GetSubComment> subComments;
    private int isHolic;
    private String updated_at;
    private String profileImg;


    public GetCommentRes(Integer id, int userId, String userName, String content, int order, int isHolic, String updated_at, String profileImg) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.order = order;
        this.isHolic = isHolic;
        this.updated_at = updated_at;
        this.profileImg = profileImg;
    }
}
