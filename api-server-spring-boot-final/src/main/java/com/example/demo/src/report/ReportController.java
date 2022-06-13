package com.example.demo.src.report;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.report.model.PostReport;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final ReportProvider provider;
    private final ReportService service;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(ReportProvider provider, ReportService service, JwtService jwtService) {
        this.provider = provider;
        this.service = service;
        this.jwtService = jwtService;
    }

    @PostMapping()
    @ResponseBody
    public BaseResponse<Integer> createReport(@RequestBody PostReport postReport) {
        if(postReport.getReviewId() == null) {
            return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
        }
        if(postReport.getReviewUserId() == null) {
            return new BaseResponse<>(REPORTS_EMPTY_USER_ID);
        }
        if(postReport.getEmail() == null){
            return new BaseResponse<>(REPORTS_EMPTY_EMAIL);
        }
        if(postReport.getReason() == null) {
            return new BaseResponse<>(REPORTS_EMPTY_REASON);
        }

        try{
            Integer userId = jwtService.getUserIdx();
            if(userId == null) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(userId == postReport.getReviewUserId()) {
                return new BaseResponse<>(USER_ID_SAME_REPORT_USER_ID);
            }
            Integer result = service.createReport(postReport, userId);
            return new BaseResponse<>(result);
        }catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

    }
}
