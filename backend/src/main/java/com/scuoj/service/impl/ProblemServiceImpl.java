package com.scuoj.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.scuoj.mapper.ProblemMapper;
import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;
import com.scuoj.service.ProblemService;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public void add(Problem problem) {
        //补充属性值
        problem.setCreateTime(LocalDateTime.now());
        problem.setUpdateTime(LocalDateTime.now());

        Map<String ,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        problem.setCreateUser(userId);

        problemMapper.add(problem);
    }

    @Override
    public List<Problem> list(Integer id, String title, Integer createUser) {
        //调用Mapper
        List<Problem> as = problemMapper.list(id,title,createUser);
        return as;
    }

    @Override
    public void delete(Integer id) {
        problemMapper.delete(id);
    }

    @Override
    public Problem getProblemInfo(Integer id) {
        return problemMapper.getProblemInfo(id);
    }

    @Override
    public Problem findByTitle(String title) {
        return problemMapper.findByTitle(title);
    }

    @Override
    public PageBean<Problem> pageList(Integer pageNum, Integer pageSize, Integer id, String title, Integer createUser) {
        //创建PageBean对象
        PageBean<Problem> pb = new PageBean<>();

        //开启分页查询PageHelper
        PageHelper.startPage(pageNum, pageSize);

        //调用Mapper
        List<Problem> as = problemMapper.list(id,title,createUser);
        //Page中提供了方法，可以获取PageHelper分页查询后 得到的总记录条数和当前页数据
        Page<Problem> p = (Page<Problem>) as;

        //将数据填充到PageBean对象中
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public void update(Problem problem) {
        problem.setUpdateTime(LocalDateTime.now());
        problemMapper.update(problem);
    }
}
