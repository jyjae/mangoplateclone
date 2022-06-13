package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.src.report.model.PostReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ReportService {
    private final ReportProvider provider;
    private final ReportDao dao;

    final Logger logger = LoggerFactory.getLogger(ReportService.class);

    public ReportService(ReportProvider provider, ReportDao dao) {
        this.provider = provider;
        this.dao = dao;
    }

    public Integer createReport(PostReport postReport, Integer userId) throws BaseException {
        if(provider.checkUser(userId)==0) {
            throw new BaseException(USERS_NOT_EXISTS_USER);
        }
        if(provider.checkUser(postReport.getReviewUserId()) == 0) {
            throw new BaseException(REPORTS_NOT_EXISTS_REVIEW_USERS);
        }
        if(provider.checkReview(postReport.getReviewId(), postReport.getReviewUserId())==0) {
            throw new BaseException(REVIEWS_NOT_EXISTS_REVIEW);
        }
        try {
            int result = dao.createReport(postReport, userId);
            if(result == 0) {
                throw new BaseException(REPORT_CREATE_FAIL);
            }
            return result;
        }catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
