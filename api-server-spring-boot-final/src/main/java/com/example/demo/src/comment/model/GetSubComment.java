package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetSubComment {
    private Integer id;
    private int userId;
    private String userName;
    private String content;
    private String parentCommentUserName;
    private int order;
    private int isHolic;
    private String profileImg;
    private String updatedAt;

    public GetSubComment(Integer id, int userId, String userName, String content, int order, String profileImg, int isHolic, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.order = order;
        this.profileImg = profileImg;
        this.isHolic = isHolic;
        this.updatedAt = updatedAt;
    }
}
