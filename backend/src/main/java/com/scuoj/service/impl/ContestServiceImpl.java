package com.scuoj.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.scuoj.mapper.ContestMapper;
import com.scuoj.pojo.Contest;
import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;
import com.scuoj.service.ContestService;
import com.scuoj.utils.ThreadLocalUtil;
import com.scuoj.utils.TimeTranfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ContestServiceImpl implements ContestService {

    @Autowired
    private ContestMapper contestMapper;

    @Override
    public void add(Contest contest) {
        //补充属性值
        contest.setCreateTime(LocalDateTime.now());
        contest.setUpdateTime(LocalDateTime.now());

        Map<String ,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        contest.setCreateUser(userId);

        //将String转换为LocalDateTime类型
        if(contest.getStartTimeStr()!=null){
            contest.setStartTime(TimeTranfer.StrToLocalDateTime(contest.getStartTimeStr()));
        }
        if(contest.getEndTimeStr()!=null){
            contest.setEndTime(TimeTranfer.StrToLocalDateTime(contest.getEndTimeStr()));
        }

        contestMapper.add(contest);
    }

    @Override
    public void update(Contest contest) {
        //补充属性值
        contest.setUpdateTime(LocalDateTime.now());
        //将String转换为LocalDateTime类型
        System.out.println(contest.getStartTimeStr());
        if(contest.getStartTimeStr()!=null){
            contest.setStartTime(TimeTranfer.StrToLocalDateTime(contest.getStartTimeStr()));
        }
        System.out.println(contest.getStartTime());
        if(contest.getEndTimeStr()!=null){
            contest.setEndTime(TimeTranfer.StrToLocalDateTime(contest.getEndTimeStr()));
        }
        System.out.println(contest.getEndTimeStr());
        System.out.println(contest.getEndTime());
        contestMapper.update(contest);
    }

    @Override
    public PageBean<Contest> pageList(Integer pageNum, Integer pageSize, Integer id, String title, Integer createUser) {
        //创建PageBean对象
        PageBean<Contest> pb = new PageBean<>();

        //开启分页查询PageHelper
        PageHelper.startPage(pageNum, pageSize);

        //调用Mapper
        List<Contest> as = contestMapper.list(id,title,createUser);
        //Page中提供了方法，可以获取PageHelper分页查询后 得到的总记录条数和当前页数据
        Page<Contest> p = (Page<Contest>) as;

        //将数据填充到PageBean对象中
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public void delete(Integer id) {
        contestMapper.delete(id);
    }

    @Override
    public Contest getContestInfo(Integer id) {
        Contest contest = contestMapper.getContestInfo(id);
        return contest;
    }

    @Override
    public Contest findByTile(String title) {
        Contest contest = contestMapper.findByTitle(title);
        return contest;
    }

    @Override
    public List<Contest> list(Integer id, String title, Integer createUser) {
        return contestMapper.list(id,title,createUser);
    }


}
