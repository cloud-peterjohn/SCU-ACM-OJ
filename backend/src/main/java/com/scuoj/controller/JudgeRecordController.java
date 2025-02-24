package com.scuoj.controller;

import com.scuoj.pojo.*;
import com.scuoj.service.*;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/JudgeRecord")
//@CrossOrigin(origins = "http://localhost:8080")
public class JudgeRecordController {

    @Autowired
    private JudgeRecordService judgeRecordService;

    @Autowired
    private ContestService contestService;

    @Autowired
    private UserService userService;

    @Autowired
    private BelongService belongService;

    @Autowired
    private JudgeTemplateService judgeTemplateService;

    //新增评测记录
    @PostMapping("/add")
    public Result add(@RequestBody @Validated JudgeRecord judgeRecord) {
        judgeRecordService.add(judgeRecord);
        return Result.success();
    }

    //通过用户ID删除评测记录
    @DeleteMapping("/deleteByUserId")
    public Result deleteByUserId(@RequestBody Integer userId) {
        //获取当前用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        User loginUser = userService.findById(loginId);

        //判断权限
        if(!loginUser.getAuth().equals("superAdmin")){
            return Result.error("权限不足");
        }

        judgeRecordService.deleteByUserId(userId);
        return Result.success("删除成功");
    }

    //通过用户ID删除评测记录列表
    @DeleteMapping("/deleteListByUserId")
    public Result deleteListByUserId(@RequestBody List<Integer> userIdList) {
        //获取当前用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        User loginUser = userService.findById(loginId);

        //判断权限
        if(!loginUser.getAuth().equals("superAdmin")){
            return Result.error("权限不足");
        }

        for (Integer userId : userIdList) {
            judgeRecordService.deleteByUserId(userId);
        }
        return Result.success("删除成功");
    }

    //通过比赛ID删除评测记录
    @DeleteMapping("/deleteByContestId")
    public Result deleteByContestId(@RequestBody Integer contestId) {
        //获取当前用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        User loginUser = userService.findById(loginId);

        //判断权限
        if(!loginUser.getAuth().equals("superAdmin")){
            return Result.error("权限不足");
        }

        judgeRecordService.deleteByContestId(contestId);
        return Result.success("删除成功");
    }

    //通过比赛ID删除评测记录列表
    @DeleteMapping("/deleteListByContestId")
    public Result deleteListByContestId(@RequestBody List<Integer> contestIdList) {
        //获取当前用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        User loginUser = userService.findById(loginId);

        //判断权限
        if(!loginUser.getAuth().equals("superAdmin")){
            return Result.error("权限不足");
        }

        for (Integer contestId : contestIdList) {
            judgeRecordService.deleteByContestId(contestId);
        }

        return Result.success("删除成功");
    }

    @GetMapping("/pageList")
    public Result<PageBean<JudgeRecord>> pageList(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer problemId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String judgeResult
    ){
        PageBean<JudgeRecord> pb = judgeRecordService.pageList(pageNum,pageSize,problemId,userId,language,judgeResult);
        return Result.success(pb);
    }

    //
    @PostMapping("/MyAllList")
    public Result MyAllList() {
        //获取当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        User loginUser = userService.findById(loginId);

        List<JudgeRecord> judgeRecordList = judgeRecordService.list(null,loginId,null,null,null);

        for (JudgeRecord judgeRecord : judgeRecordList) {
            JudgeTemplate judgeTemplate = judgeTemplateService.getById(judgeRecord.getTemplateId());
            judgeRecord.setTemplateName(judgeTemplate.getLanguage());
        }

        return Result.success(judgeRecordList);
    }


    //获取评测纪录列表
    @GetMapping("/list")
    public Result<List<JudgeRecord>> list(
            @RequestParam(required = false) Integer problemId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String judgeResult,
            @RequestParam(required = false) Integer contestId,
            @RequestParam(required = false) String username
    ){
        if(username!=null){
            User u = userService.findByUserName(username);
            userId = u.getId();
        }

        List<JudgeRecord> pb = judgeRecordService.list(problemId,userId,language,judgeResult,contestId);
        return Result.success(pb);
    }

    //获得自己关于某比赛所有题目的评测记录
    @PostMapping("/MyList")
    public Result<List<JudgeRecord>> MyList(Integer contestId){

//        System.out.println(contestId);
        //获取当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");

        //获取自己关于该比赛的所有评测记录
        List<JudgeRecord> judgeRecordList = judgeRecordService.list(null,loginId,null,null,contestId);
        judgeRecordList.sort((judgeRecord1, judgeRecord2) -> {
            return judgeRecord2.getId().compareTo(judgeRecord1.getId()); // 注意这里的顺序，我们先传入较大的ID
        });

        //获得该比赛下的所有题目
        List<Problem> pb = belongService.listProblem(contestId);

        pb.sort((problem1, problem2) -> {
            return problem1.getId().compareTo(problem2.getId());
        });

        List<JudgeRecord> res = new ArrayList<>();

        for (Problem problem : pb) {
            JudgeRecord jr = new JudgeRecord();
            for (JudgeRecord jun : judgeRecordList) {
                if(jun.getProblemId().equals(problem.getId())){
                    jr = jun;
                    break;
                }
            }
            jr.setProblemId(problem.getId());
            jr.setProblemTitle(problem.getTitle());
            res.add(jr);
        }

        return Result.success(res);
    }

    //返回某比赛中所有题目的评测结果
    @GetMapping("/MyResultList")
    public Result<List<JudgeResult>> MyResultList(Integer contestId){
        //获取当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");

        //获取自己关于该比赛的所有评测记录
        List<JudgeRecord> jr = judgeRecordService.list(null,loginId,null,null,contestId);

        //获得该比赛下的所有题目
        List<Problem> pb = belongService.listProblem(contestId);

        //最终返回的结果
        List<JudgeResult> judgeResultList = new ArrayList<>();

        for (Problem p : pb) {//循环比赛题目
            //获取当前题目的返回结果
            JudgeResult judgeResult = new JudgeResult();
            judgeResult.setTitle(p.getTitle());
            judgeResult.setProblemId(p.getId());

            List<JudgeRecord> jrr = new ArrayList<JudgeRecord>();
            for (JudgeRecord j : jr) {//获得该题目的所有评测记录
                if(j.getProblemId().equals(p.getId())){
                    jrr.add(j);
                }
            }
            int cnt = 0;//AC前错误次数
            boolean IsAC = false;//是否AC
            //循环查找，直到AC
            for (JudgeRecord j : jrr) {
                if(j.getJudgeResult().equals("AC")){
                    IsAC = true;
                    judgeResult.setAcTime(j.getJudgeTime());
                    break;
                }
                if(!j.getJudgeResult().equals("CE")) cnt++;
            }
            if(IsAC){
                judgeResult.setResult("AC");
            }
            else if(cnt==0){
                judgeResult.setResult("无有效提交");
            }
            else{
                judgeResult.setResult("WA");
            }
            judgeResult.setCnt(cnt);

            judgeResultList.add(judgeResult);
        }
        return Result.success(judgeResultList);
    }
}
