package com.example.demo.src.oauth.kakao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class KakaoDao {
    @Autowired private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(KakaoDao.class);

    public int checkUsers(String email) {
        String checkUsers = "select exists (select * from users where email = ? and social_provider = 'KAKAO' and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUsers, Integer.class, email);
    }

    public Integer createUser(String email, String nickName, String profileImage) {
        String createUserQuery = "insert into users(email, social_provider, status, user_name, profile_img_url)" +
                " values(?, 'KAKAO', 'ACTIVE', ?, ?)";

        jdbcTemplate.update(createUserQuery, email, nickName, profileImage);

        String lastUserId = "select id from users order by id desc limit 1";

        return jdbcTemplate.queryForObject(lastUserId, Integer.class);
    }

    public int getUserId(String email) {
        String checkUsers = "select id from users where email = ? and social_provider = 'KAKAO' and status = 'ACTIVE'";
        return jdbcTemplate.queryForObject(checkUsers, int.class, email);
    }
}
