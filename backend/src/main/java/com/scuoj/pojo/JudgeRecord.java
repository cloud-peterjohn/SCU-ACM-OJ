package com.scuoj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JudgeRecord {
    private String code;//代码
    private String language;//代码语言
    private Integer id;//主键ID
    private Integer problemId;//题目ID
    private Integer userId;//用户ID
    private Integer templateId;//评测模板ID
    private String templateName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime judgeTime;//评测时间
    private String judgeResult = "null";//评测结果
    private Integer runTime = 0;//运行时间，单位毫秒
    private Integer runMemory = 0;//运行内存，单位KB
    private String compileMessage;//编译信息
    private Integer contestId;//比赛ID

    private String problemTitle;
    private String username;

    private Boolean isAc;

    private String result;//报错时的


}
