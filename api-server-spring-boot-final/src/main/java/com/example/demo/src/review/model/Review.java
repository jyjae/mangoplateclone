package com.example.demo.src.review.model;

import com.example.demo.src.review.upload.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
public class Review {
    private String content;
    private Integer score;
    private  List<UploadFile> file;

}
