package com.scuoj.service;

import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;

import java.util.List;

public interface ProblemService {

    //添加问题
    void add(Problem problem);

    List<Problem> list(Integer id, String title, Integer createUser);

    //根据题目ID删除题目
    void delete(Integer id);

    //查看题目信息
    Problem getProblemInfo(Integer id);

    //通过题目标题查询题目
    Problem findByTitle(String title);

    PageBean<Problem> pageList(Integer pageNum, Integer pageSize, Integer id, String title, Integer createUser);

    //更新题目
    void update(Problem problem);
}
