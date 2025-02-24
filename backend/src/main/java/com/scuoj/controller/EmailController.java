package com.scuoj.controller;

import com.scuoj.pojo.Result;
import com.scuoj.service.EmailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
//@CrossOrigin(origins = "http://localhost:8080")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 发送邮件验证码
     *
     * @param email 用户邮箱
     */
    @PostMapping("/sendEmailCode")
    public Result sendEmailCode( @RequestParam String email) {
        emailService.sendEmailCode(email);
        return Result.success();
    }

    /**
     * 检查邮箱验证码
     *
     * @param email 用户邮箱
     * @param code  验证码
     * @return 是否正确
     */
    @PostMapping("/checkEmailCode")
    public boolean checkEmailCode(@RequestParam String email, @RequestParam String code) {
        return emailService.checkEmailCode(email, code);
    }
}
