package com.scuoj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 无他用
 * 仅作为某个接口的返回值
 */
@Data
public class JudgeResult {
    private String result = "Default";
    private Integer problemId;
    private char newId;
    private String title;//比赛标题
    private Integer cnt = 0;//AC前错误个数
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime acTime;//AC时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;//最后一次提交时间，AC后不变化
}
