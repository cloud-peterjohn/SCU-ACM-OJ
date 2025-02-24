package com.scuoj.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.scuoj.mapper.JudgeRecordMapper;
import com.scuoj.mapper.ProblemMapper;
import com.scuoj.mapper.UserMapper;
import com.scuoj.pojo.JudgeRecord;
import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;
import com.scuoj.pojo.User;
import com.scuoj.service.JudgeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class JudgeRecordServiceImpl implements JudgeRecordService {

    @Autowired
    JudgeRecordMapper judgeRecordMapper;

    @Autowired
    ProblemMapper problemMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public void add(JudgeRecord judgeRecord) {
        //新添属性
        judgeRecord.setJudgeTime(LocalDateTime.now());
        judgeRecordMapper.add(judgeRecord);
    }

    @Override
    public PageBean<JudgeRecord> pageList(Integer pageNum, Integer pageSize, Integer problemId, Integer userId, String language, String judgeResult) {
        //创建PageBean对象
        PageBean<JudgeRecord> pb = new PageBean<>();

        //开启分页查询PageHelper
        PageHelper.startPage(pageNum, pageSize);

        Integer contestId = 1;
        //调用Mapper
        List<JudgeRecord> as = judgeRecordMapper.list(problemId,userId,language,judgeResult,contestId);
        //Page中提供了方法，可以获取PageHelper分页查询后 得到的总记录条数和当前页数据
        Page<JudgeRecord> p = (Page<JudgeRecord>) as;

        //将数据填充到PageBean对象中
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public List<JudgeRecord> list(Integer problemId, Integer userId, String language, String judgeResult,Integer contestId) {
        List<JudgeRecord> as = judgeRecordMapper.list(problemId,userId,language,judgeResult,contestId);
        for(JudgeRecord judgeRecord : as){
            Problem problem = problemMapper.getProblemInfo(judgeRecord.getProblemId());
            User user = userMapper.findById(judgeRecord.getUserId());
            judgeRecord.setProblemTitle(problem.getTitle());
            judgeRecord.setUsername(user.getUsername());
        }
        return as;
    }

    @Override
    public void update(JudgeRecord judgeRecord) {
        judgeRecordMapper.update(judgeRecord);
    }

    @Override
    public void deleteByUserId( Integer userId ) {
        judgeRecordMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByContestId( Integer contestId ) {
        judgeRecordMapper.deleteByContestId(contestId);
    }
}
