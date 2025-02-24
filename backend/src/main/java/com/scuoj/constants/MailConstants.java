package com.scuoj.constants;

public interface MailConstants {
    /**
     * 邮箱验证码超时时间（分钟为单位）
     */
    Integer EMAIL_CODE_TIME_OUT = 5;
    /**
     * 邮箱验证码位数
     */
    Integer EMAIL_CODE_NUM = 6;
    /**
     * 邮箱头模版
     */
    String EMAIL_CODE_TEMPLATE_SUBJECT = "来自scuoj发送的验证码";
}
