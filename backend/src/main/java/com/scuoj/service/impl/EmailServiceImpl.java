package com.scuoj.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.scuoj.constants.FreemarkerConstants;
import com.scuoj.constants.MailConstants;
import com.scuoj.constants.RedisKeyConstants;
import com.scuoj.dto.SendCodeFtlDto;
import com.scuoj.service.EmailService;
import com.scuoj.utils.RedisUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    private final Configuration configuration;

    /**
     * 使用邮箱发送验证码
     *
     * @param email 需要送达的邮箱
     */
    @Override
    public void sendEmailCode(String email) {
        // 1. 生成验证码
        String code = RandomUtil.randomString(MailConstants.EMAIL_CODE_NUM);
        // 2. 构建 SendCodeFtlDto 对象
        SendCodeFtlDto sendCodeFtlDto = SendCodeFtlDto.builder()
                .companyName("scuoj")
                .validTime(MailConstants.EMAIL_CODE_TIME_OUT)
                .verifyCode(code)
                .build();

        // 3. 异步发送邮件验证码
        CompletableFuture.runAsync(() -> {
            try (StringWriter stringWriter = new StringWriter()) {
                // 4. 加载模板
                Template template = configuration.getTemplate(FreemarkerConstants.EMAIL_SEND_CODE_FTL_PATH);
                template.process(sendCodeFtlDto, stringWriter);

                // 5. 发送邮件
                sendEmailWithHtmlContent(email, MailConstants.EMAIL_CODE_TEMPLATE_SUBJECT, stringWriter.toString());
            } catch (Exception e) {
                log.error("发送失败", e);
                throw new RuntimeException(e);
            }
        }).thenRunAsync(() -> {
            // 6. 将验证码存入 Redis
            redisUtil.addCacheZSetValue(
                    RedisKeyConstants.EMAIL_CODE_CACHE_PREFIX + email,
                    code.toUpperCase(),
                    Instant.now().toEpochMilli(),
                    sendCodeFtlDto.getValidTime(),
                    TimeUnit.MINUTES
            );
        });
    }

    /**
     * 校验邮箱验证码
     *
     * @param email     邮箱
     * @param emailCode 验证码
     * @return 是否校验成功
     */
    @Override
    public boolean checkEmailCode(String email, String emailCode) {
        Instant currentTime = Instant.now();
        Instant adjustedTime = currentTime.minus(Duration.ofMinutes(MailConstants.EMAIL_CODE_TIME_OUT));

        String redisEmailCodeKey = RedisKeyConstants.EMAIL_CODE_CACHE_PREFIX + email;
        Set<String> cacheZSetByScore = redisUtil.getCacheZSetByScore(
                redisEmailCodeKey,
                adjustedTime.toEpochMilli(),
                currentTime.toEpochMilli(),
                0,
                -1
        );

        boolean result = cacheZSetByScore.contains(emailCode.toUpperCase());
        if (result) {
            redisUtil.deleteObject(redisEmailCodeKey);
        }
        return result;
    }

    /**
     * 发送包含
     * HTML 内容的邮件
     *
     * @param sendToEmail 收件人邮箱地址
     * @param subject     邮件主题
     * @param html        HTML 格式的邮件内容
     * @throws MessagingException 发送邮件过程中可能抛出的异常
     */
    private void sendEmailWithHtmlContent(String sendToEmail, String subject, String html) throws MessagingException {
        // 创建 MimeMessage 对象
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // 使用 MimeMessageHelper 设置邮件属性
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        mimeMessageHelper.setFrom("scuoj_demo@163.com"); // 设置发件人邮箱
        mimeMessageHelper.setTo(sendToEmail);             // 设置收件人邮箱
        mimeMessageHelper.setSubject(subject);            // 设置邮件主题
        mimeMessageHelper.setText(html, true);            // 设置邮件内容

        // 发送邮件
        javaMailSender.send(mimeMessage);
    }
}
