package com.scuoj.service.impl;

import com.scuoj.mapper.BelongMapper;
import com.scuoj.mapper.ContestMapper;
import com.scuoj.mapper.ProblemMapper;
import com.scuoj.pojo.Belong;
import com.scuoj.pojo.Contest;
import com.scuoj.pojo.Problem;
import com.scuoj.service.BelongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BelongServiceImpl implements BelongService {

    @Autowired
    private BelongMapper belongMapper;

    @Autowired
    private ContestMapper contestMapper;

    @Autowired
    private ProblemMapper problemMapper;

    @Override
    public void add(Integer problemId, Integer contestId) {
        System.out.println(problemId);
        System.out.println(contestId);
        belongMapper.add(problemId,contestId);
    }

    @Override
    public Contest findByProblemId(Integer problemId) {
        Integer contestId = belongMapper.findByProblemId(problemId);
        return contestMapper.getContestInfo(contestId);
    }

    @Override
    public List<Problem> listProblem(Integer contestId) {
        List<Integer> pId = belongMapper.list(contestId);
        List<Problem> pList = new ArrayList<>();
        for(Integer pId1 : pId) {
            pList.add(problemMapper.getProblemInfo(pId1));
        }
        return pList;
    }

    @Override
    public Belong getById(Integer problemId, Integer contestId) {
        return belongMapper.getById(problemId,contestId);
    }

    @Override
    public void delete(Integer problemId) {
        belongMapper.delete(problemId);
    }

    @Override
    public List<Problem> allProblem() {
        List<Integer> ProblemId = belongMapper.allproblem();
        List<Problem> pList = new ArrayList<>();
        for(Integer ProblemId1 : ProblemId) {
            pList.add(problemMapper.getProblemInfo(ProblemId1));
        }
        return pList;
    }
}
