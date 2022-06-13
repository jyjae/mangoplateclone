package com.example.demo.src.comment.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentReq {
    private Integer reviewId;
    private Integer commentId;
    private Integer parentUserId;
    private String comment;
}
