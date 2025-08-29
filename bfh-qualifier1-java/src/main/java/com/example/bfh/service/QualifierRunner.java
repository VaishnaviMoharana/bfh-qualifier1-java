package com.example.bfh.service;

import com.example.bfh.config.AppProps;
import com.example.bfh.dto.FinalQueryRequest;
import com.example.bfh.dto.GenerateWebhookRequest;
import com.example.bfh.dto.GenerateWebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class QualifierRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(QualifierRunner.class);

    private final AppProps props;
    private final HttpClient http;

    private static final String FINAL_SQL_Q1 =
        "SELECT " +
        "  p.AMOUNT AS SALARY, " +
        "  e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME, " +
        "  EXTRACT(YEAR FROM AGE(CURRENT_DATE, e.DOB)) AS AGE, " +
        "  d.DEPARTMENT_NAME " +
        "FROM PAYMENTS p " +
        "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
        "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
        "WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) <> 1 " +
        "ORDER BY p.AMOUNT DESC " +
        "LIMIT 1;";

    public QualifierRunner(AppProps props, HttpClient http) {
        this.props = props;
        this.http = http;
    }

    @Override
    public void run(String... args) {
        log.info("== BFH Qualifier – Startup flow ==");

        var genReq = new GenerateWebhookRequest(props.getName(), props.getRegNo(), props.getEmail());
        GenerateWebhookResponse genResp = http.postJson(props.getGenerateUrl(), genReq, GenerateWebhookResponse.class)
                .doOnError(err -> log.error("Failed to generate webhook: {}", err.toString()))
                .block();

        if (genResp == null || genResp.getAccessToken() == null) {
            throw new IllegalStateException("No accessToken returned from generateWebhook");
        }

        String webhookUrl = (genResp.getWebhook() != null && !genResp.getWebhook().isBlank())
                ? genResp.getWebhook()
                : props.getFallbackSubmitUrl();

        log.info("Webhook resolved to: {}", webhookUrl);

        int lastTwo = extractLastTwoDigits(props.getRegNo());
        boolean isOdd = (lastTwo % 2 != 0);
        if (!isOdd) {
            throw new IllegalStateException("This build implements Question 1 (odd regNo) only. Got even: " + lastTwo);
        }

        var submitReq = new FinalQueryRequest(FINAL_SQL_Q1);
        String submitEcho = http.postJsonWithAuth(webhookUrl, genResp.getAccessToken(), submitReq, String.class)
                .doOnError(err -> log.error("Submission failed: {}", err.toString()))
                .block();

        log.info("Submission response: {}", submitEcho);
        log.info("== Completed ==");
    }

    private int extractLastTwoDigits(String regNo) {
        String digits = regNo.replaceAll("\D+", "");
        if (digits.length() < 2) throw new IllegalArgumentException("regNo must have 2 digits");
        String last2 = digits.substring(digits.length() - 2);
        return Integer.parseInt(last2);
    }
}
