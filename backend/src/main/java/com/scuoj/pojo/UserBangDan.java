package com.scuoj.pojo;

import lombok.Data;

import java.util.List;

@Data
public class UserBangDan {
    private int userId;
    private String userName;
    private List<JudgeResult> judgeResultList;//评测结果列表，包含了某场比赛下的所有题目
    private Integer timePenalty = 0;//罚时
    private Integer acNum = 0;//AC数量
    private Integer rank = 0;
    private Integer problemNum = 0;
}
