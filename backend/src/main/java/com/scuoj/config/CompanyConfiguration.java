package com.scuoj.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@ConfigurationProperties(prefix = "company")
public class CompanyConfiguration {
    /**
     * 公司名
     */
    private String name;
    /**
     * 公司邮箱
     */
    private String email;
    /**
     * 公司官网
     */
    private String website;
}
