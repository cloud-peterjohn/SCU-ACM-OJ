package com.scuoj.service;

import com.scuoj.pojo.Belong;
import com.scuoj.pojo.Contest;
import com.scuoj.pojo.Problem;

import java.util.List;

public interface BelongService {

    //添加一条题目归属
    void add(Integer problemId, Integer contestId);

    //通过题目查找对应比赛
    Contest findByProblemId(Integer problemId);

    //通过比赛Id查找对应
    List<Problem> listProblem(Integer contestId);

    //通过题目Id和比赛Id唯一确定一个Belong表
    Belong getById(Integer problemId, Integer contestId);

    //通过题目ID删除归属表
    void delete(Integer problemId);

    //获取所有的题目
    List<Problem> allProblem();
}
