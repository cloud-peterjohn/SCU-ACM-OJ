package com.scuoj.dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SendCodeFtlDto {
    /**
     * 公司网址
     */
    public String companyWebsite;
    /**
     * 公司名
     */
    public String companyName;
    /**
     * 验证码内容
     */
    public String verifyCode;
    /**
     * 验证码有效时长 ( 以分钟为单位 )
     */
    public Integer validTime;
}

