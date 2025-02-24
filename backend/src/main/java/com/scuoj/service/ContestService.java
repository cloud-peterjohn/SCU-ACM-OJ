package com.scuoj.service;

import com.scuoj.pojo.Contest;
import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;

import java.util.List;

public interface ContestService {

    //新增比赛
    void add(Contest contest);

    //更新比赛信息
    void update(Contest contest);

    //分页查询比赛列表
    PageBean<Contest> pageList(Integer pageNum, Integer pageSize, Integer id, String title, Integer createUser);

    //删除比赛
    void delete(Integer id);

    //查看比赛信息
    Contest getContestInfo(Integer id);

    //通过比赛标题查询比赛
    Contest findByTile(String title);

    //直接返回list
    List<Contest> list(Integer id, String title, Integer createUser);
}
