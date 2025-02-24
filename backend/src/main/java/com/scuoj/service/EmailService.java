package com.scuoj.service;

public interface EmailService {
    /**
     * 发送邮件验证码
     *
     * @param email 用户邮箱
     */
    void sendEmailCode(String email);

    /**
     * 检查邮箱验证码
     *
     * @param email 用户邮箱
     * @param code  验证码
     * @return 是否正确
     */
    boolean checkEmailCode(String email, String code);
}
