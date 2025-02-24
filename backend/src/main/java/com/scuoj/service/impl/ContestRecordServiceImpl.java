package com.scuoj.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.scuoj.mapper.ContestMapper;
import com.scuoj.mapper.ContestRecordMapper;
import com.scuoj.mapper.UserMapper;
import com.scuoj.pojo.*;
import com.scuoj.service.ContestRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestRecordServiceImpl implements ContestRecordService {

    @Autowired
    private ContestRecordMapper contestRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ContestMapper contestMapper;

    @Override
    public PageBean<ContestRecord> pageList(Integer pageNum, Integer pageSize, Integer contestId, Integer userId) {
        //创建PageBean对象
        PageBean<ContestRecord> pb = new PageBean<>();

        //开启分页查询PageHelper
        PageHelper.startPage(pageNum, pageSize);

        User u = userMapper.findById(userId);
        String username = u.getUsername();

        //调用Mapper
        List<ContestRecord> as = contestRecordMapper.list(contestId,userId);
        //Page中提供了方法，可以获取PageHelper分页查询后 得到的总记录条数和当前页数据
        Page<ContestRecord> p = (Page<ContestRecord>) as;

        //将数据填充到PageBean对象中
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public void add(ContestRecord contestRecord) {
        contestRecordMapper.add(contestRecord);
    }

    @Override
    public void update(ContestRecord contestRecord) {
        contestRecordMapper.update(contestRecord);
    }

    @Override
    public List<ContestRecord> list(Integer contestId, Integer userId) {
        List<ContestRecord> as = contestRecordMapper.list(contestId,userId);
        if(as.isEmpty()) return as;
        Contest contest = contestMapper.getContestInfo(as.get(0).getContestId());
        System.out.println(contest);
        for(ContestRecord contestRecord : as){
            Integer nowUserId = contestRecord.getUserId();
            User user = userMapper.findById(nowUserId);
            contestRecord.setUsername(user.getUsername());
            contestRecord.setContestTitle(contest.getTitle());
        }
        return as;
    }

    @Override
    public void deleteByUserId( Integer userId ) {
        contestRecordMapper.deleteByUserId(userId);
    }

    @Override
    public List<Integer> userListHavingContest(Integer contestId) {
        return contestRecordMapper.userListHavingContest(contestId);
    }

    @Override
    public List<ContestRecord> listByUserId( Integer userId ) {
        return contestRecordMapper.listByUserId(userId);
    }

//    @Override
//    public Object findUserByContestId( Integer contestId ) {
//        return contestRecordMapper.findUserByContestId(contestId);
//    }
}
