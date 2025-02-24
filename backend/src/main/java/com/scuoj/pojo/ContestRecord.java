package com.scuoj.pojo;

import lombok.Data;

@Data
public class ContestRecord {
    private Integer id;//比赛记录
    private Integer contestId;//比赛ID
    private Integer userId;//用户ID
    private Integer ranking = 0;//排名
    private Integer acNum = 0;//过题数量
    private Integer allTime = 0;//总的罚时

    private String contestTitle;
    private String username;
}
