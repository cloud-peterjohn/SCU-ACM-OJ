package com.scuoj.service;

import com.scuoj.pojo.ContestRecord;
import com.scuoj.pojo.PageBean;

import java.util.List;

public interface ContestRecordService {

    PageBean<ContestRecord> pageList(Integer pageNum, Integer pageSize, Integer contestId, Integer userId);

    //新增比赛记录
    void add(ContestRecord contestRecord);

    //更新比赛记录
    void update(ContestRecord contestRecord);

    //返回列表
    List<ContestRecord> list(Integer contestId, Integer userId);

    //通过用户ID删除比赛记录
    void deleteByUserId( Integer userId );

    //获取已经添加至比赛的用户列表
    List<Integer> userListHavingContest(Integer contestId);

    //找到当前用户的比赛
    List<ContestRecord> listByUserId( Integer userId );

//    //获得比赛中已经存在的用户
//    Object findUserByContestId( Integer contestId );
}
