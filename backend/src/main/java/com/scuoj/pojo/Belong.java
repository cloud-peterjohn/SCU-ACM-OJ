package com.scuoj.pojo;

import lombok.Data;

@Data
public class Belong {
    private Integer problemId;//题目ID
    private Integer contestId;//比赛ID
    private String problemTitle;
    private String contestTitle;
}
