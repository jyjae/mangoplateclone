package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class ReportProvider {
    private final ReportDao dao;

    final Logger logger = LoggerFactory.getLogger(ReportProvider.class);

    public ReportProvider(ReportDao dao) {
        this.dao = dao;
    }

    public int checkUser(int userId) throws BaseException {
        try {
            return dao.checkUser(userId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkReview(Integer reviewId,  Integer reviewUserId) throws BaseException {
        try {
            return dao.checkReviewId(reviewId, reviewUserId);
        }catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
