package com.example.bfh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "bfh")
public class AppProps {
    private String name;
    private String regNo;
    private String email;
    private String generateUrl;
    private String fallbackSubmitUrl;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGenerateUrl() { return generateUrl; }
    public void setGenerateUrl(String generateUrl) { this.generateUrl = generateUrl; }

    public String getFallbackSubmitUrl() { return fallbackSubmitUrl; }
    public void setFallbackSubmitUrl(String fallbackSubmitUrl) { this.fallbackSubmitUrl = fallbackSubmitUrl; }
}
