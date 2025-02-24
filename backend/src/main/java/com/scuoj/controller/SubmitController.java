package com.scuoj.controller;

import com.scuoj.pojo.JudgeRecord;
import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import com.scuoj.service.JudgeRecordService;
import com.scuoj.service.SubmitService;
import com.scuoj.utils.OjElemUtil;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/submit")
//@CrossOrigin(origins = "http://localhost:8080")
public class SubmitController {

    @Autowired
    private SubmitService submitService;

    @Autowired
    private JudgeRecordService judgeRecordService;

    //提交
    @PostMapping
    public Result submit(@RequestBody JudgeRecord judgeRecord) {
//        judgeRecord.setJudgeTime(LocalDateTime.now());

        try {
            //获取当前用户
            Map<String,Object> map = ThreadLocalUtil.get();
            Integer loginUserId = (Integer) map.get("id");
            judgeRecord.setUserId(loginUserId);
            judgeRecord.setJudgeResult("PE");
            //评测时间
            judgeRecord.setJudgeTime(LocalDateTime.now());
//            judgeRecordService.add(judgeRecord);

            JudgeRecord judgeRecord1 = submitService.submit(judgeRecord);
            judgeRecord1.setResult("success");
            return Result.success(judgeRecord1);
        }
        catch (Exception e) {
            e.printStackTrace();
            JudgeRecord record = new JudgeRecord();
            record.setCompileMessage(e.getMessage());
            record.setJudgeTime(LocalDateTime.now());
            record.setResult("error");
            return Result.error(e.getMessage());
        }
    }
}
