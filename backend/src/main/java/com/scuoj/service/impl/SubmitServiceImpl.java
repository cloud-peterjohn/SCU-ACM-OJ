package com.scuoj.service.impl;

import com.scuoj.pojo.JudgeRecord;
import com.scuoj.pojo.JudgeTemplate;
import com.scuoj.pojo.Problem;
import com.scuoj.service.JudgeRecordService;
import com.scuoj.service.JudgeTemplateService;
import com.scuoj.service.ProblemService;
import com.scuoj.service.SubmitService;
import com.scuoj.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmitServiceImpl implements SubmitService {

    @Autowired
    JudgeRecordService judgeRecordService;

    @Autowired
    ProblemService problemService;

    @Autowired
    JudgeTemplateService judgeTemplateService;

    @Override
    public JudgeRecord submit(JudgeRecord judgeRecord) {
        Problem problem = problemService.getProblemInfo(judgeRecord.getProblemId());
//        System.out.println(judgeRecord);
        //调用接口检测是否包含危险代码
        AiUtil aiUtil = new AiUtil();
        String STATUS =  aiUtil.aiCaller(judgeRecord.getCode());
        System.out.println(STATUS);
        //如果不包含危险代码，则进行评测
        if(STATUS.equals("False")||STATUS.equals("\"False\"")){
            JudgeUtil.Judge(judgeRecord, judgeRecord.getCode(), judgeRecord.getLanguage(), problem.getProblemUrl(), problem.getTimeLimit(),problem.getMemoryLimit());

        }
        //如果包含危险代码，则将setRunTime和setRunMemory设置为0，setJudgeResult为WA
        else {
            judgeRecord.setRunTime(0);
            judgeRecord.setRunMemory(0);
            judgeRecord.setJudgeResult("WA");
        }
        //获得评测模板
        JudgeTemplate judgeTemplate = judgeTemplateService.findByLanguage(judgeRecord.getLanguage());
        //设置评测模板ID
        judgeRecord.setTemplateId(judgeTemplate.getId());
//        System.out.println(judgeRecord);

        judgeRecord.setJudgeTime(LocalDateTime.now());

        System.out.println(judgeRecord.getRunMemory());

        judgeRecordService.add(judgeRecord);
        return judgeRecord;
    }
}
