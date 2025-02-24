package com.scuoj.service;

import com.scuoj.pojo.JudgeRecord;
import com.scuoj.pojo.PageBean;

import java.util.List;

public interface JudgeRecordService {

    //新增评测记录
    void add(JudgeRecord judgeRecord);

    //分页查询
    PageBean<JudgeRecord> pageList(Integer pageNum, Integer pageSize, Integer problemId, Integer userId, String language, String judgeResult);

    //返回列表
    List<JudgeRecord> list(Integer problemId, Integer userId, String language, String judgeResult,Integer contestId);

    //更新评测记录
    void update(JudgeRecord judgeRecord);

    //通过用户ID删除评测记录
    void deleteByUserId( Integer userId );

    //通过比赛ID删除评测记录
    void deleteByContestId( Integer contestId );
}
