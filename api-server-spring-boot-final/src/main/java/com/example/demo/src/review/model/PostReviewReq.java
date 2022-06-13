package com.example.demo.src.review.model;

import com.example.demo.src.review.upload.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PostReviewReq {
    private String content;
    private Integer score;
    private  List<MultipartFile> file;
}
