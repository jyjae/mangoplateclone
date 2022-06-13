package com.example.demo.src.oauth.facebook.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FacebookUser {
    private int id;
    private String email;
    private String userName;
    private String SocialProvider;
}
