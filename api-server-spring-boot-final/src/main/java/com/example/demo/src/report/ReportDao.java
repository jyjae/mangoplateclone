package com.example.demo.src.report;

import com.example.demo.src.report.model.PostReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao {
    @Autowired private JdbcTemplate jdbcTemplate;

    public int checkUser(Integer userId) {
        String checkUserQuery = "select exists (select * from users where id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkUserQuery, int.class, userId);
    }

    public int checkReviewId(int reviewId, int reviewUserId) {
        String checkReviewQuery = "select exists (select * from reviews where id = ? and user_id = ? and status = 'ACTIVE')";
        return jdbcTemplate.queryForObject(checkReviewQuery, int.class, reviewId, reviewUserId);
    }

    public Integer createReport(PostReport postReport, Integer userId) {
        String createReportQuery = "insert into report(user_id, review_id, email, reason, status) " +
                "values(?,?,?,?,'ACTIVE')";
        Object[] createReportParams = new Object[]{userId, postReport.getReviewId(), postReport.getEmail(), postReport.getReason()};
        return jdbcTemplate.update(createReportQuery,createReportParams);
    }
}
