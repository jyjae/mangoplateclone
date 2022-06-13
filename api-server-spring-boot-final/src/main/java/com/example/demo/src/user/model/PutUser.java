package com.example.demo.src.user.model;


import com.example.demo.src.review.upload.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class PutUser {
    private Integer userIdx;
    private String phoneNumber;
    private String userName;
    private UploadFile fileUrl;

}