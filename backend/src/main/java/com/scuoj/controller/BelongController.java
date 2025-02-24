package com.scuoj.controller;

import com.scuoj.pojo.Belong;
import com.scuoj.pojo.Contest;
import com.scuoj.pojo.Problem;
import com.scuoj.pojo.Result;
import com.scuoj.service.BelongService;
import com.scuoj.service.ContestService;
import com.scuoj.service.ProblemService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/contestProblem")
//@CrossOrigin(origins = "http://localhost:8080")
public class BelongController {

    @Autowired
    private BelongService belongService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private ContestService contestService;

    //获取题目和比赛的对应关系
    @GetMapping("/getBelong")
    public Belong getById(Integer problemId,Integer contestId) {
        return belongService.getById(problemId,contestId);
    }

    //通过比赛ID和题目ID新增题目
    @PostMapping("/addByProblemId")
    public Result add(Integer problemId,Integer contestId) {
        if(problemService.getProblemInfo(problemId) == null) {
            return Result.error("题目不存在");
        }
        if(contestService.getContestInfo(contestId) == null) {
            return Result.error("比赛不存在");
        }
        if(belongService.getById(problemId,contestId)!=null){
            return Result.error("该题目已经被其他比赛占用");
        }

//        Problem problem = problemService.getProblemInfo(problemId);

//        System.out.println(1);
//        if(!problem.isFile()){
//            System.out.println("该题目不完整");
//            return Result.error("该题目不完整");
//        }

        Contest contest = contestService.getContestInfo(contestId);
        if(contest.getEndTime().isBefore(LocalDateTime.now())){
            return Result.error("比赛已经结束");
        }

        belongService.add(problemId,contestId);
        return Result.success();
    }

    //通过题目ID获取对应比赛
    @GetMapping("/findByProblemId")
    public Result findByProblemId(Integer problemId) {
        Contest contest = belongService.findByProblemId(problemId);
        if(contest==null){
            return Result.error("该题目无对应比赛");
        }
        return Result.success(contest);
    }

    //获取一个比赛下的所有题目
    @GetMapping("/list")
    public Result<List<Problem>> list( Integer contestId) {
//        System.out.println("*****"+contestId);
        List<Problem> ps = belongService.listProblem(contestId);
        return Result.success(ps);
    }

    //删除
    @DeleteMapping("/delete")
    public Result delete(@RequestBody Integer problemId) {
        belongService.delete(problemId);
        return Result.success();
    }

    @DeleteMapping("/deleteList")
    public Result deleteList(@RequestBody List<Integer> problemIdList) {
        for (Integer problemId : problemIdList) {
            belongService.delete(problemId);
        }
        return Result.success();
    }

    //获取表中已有的题目
    @GetMapping("/allProblem")
    public List<Problem> aLLProblem(){
        List<Problem> ps = belongService.allProblem();
        return ps;
    }

}
