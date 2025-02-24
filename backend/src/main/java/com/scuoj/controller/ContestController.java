package com.scuoj.controller;

import com.scuoj.pojo.*;
import com.scuoj.service.ContestRecordService;
import com.scuoj.service.ContestService;
import com.scuoj.service.UserService;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contest")
//@CrossOrigin(origins = "http://localhost:8080")
public class ContestController {

    @Autowired
    private ContestService contestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContestRecordService contestRecordService;

    //添加比赛
    @PostMapping("/add")
    public Result add(@RequestBody @Validated Contest contest) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        Contest contest1 = contestService.findByTile(contest.getTitle());
        if(contest1 != null){
            return Result.error("比赛名称已存在");
        }

        contestService.add(contest);
        return Result.success();
    }

    //更新比赛信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated Contest contest) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        Contest contest2 = contestService.getContestInfo(contest.getId());

        if(!contest2.getTitle().equals(contest.getTitle())) {
            Contest contest1 = contestService.findByTile(contest.getTitle());
            if (contest1 != null) {
                return Result.error("比赛名称已存在");
            }
        }

        contestService.update(contest);
        return Result.success();
    }

    //分页查询比赛题目
    @GetMapping("/pageList")
    public Result<PageBean<Contest>> pageList(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer createUser
    ){
        PageBean<Contest> pb = contestService.pageList(pageNum,pageSize,id,title,createUser);
        return Result.success(pb);
    }

    //返回列表
    @GetMapping("/list")
    public Result<List<Contest>> list(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer createUser
    ){
        List<Contest> pb = contestService.list(id,title,createUser);
        return Result.success(pb);
    }

    //删除比赛
    @DeleteMapping("/delete")
    public Result delete(Integer id) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        contestService.delete(id);
        return Result.success();
    }

    //删除比赛列表
    @DeleteMapping("/deleteList")
    public Result deleteList(@RequestBody List<Integer> idList) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        for(Integer id : idList){
            contestService.delete(id);
        }

        return Result.success();
    }

    //查看比赛信息
    @GetMapping("/ContestInfo")
    public Result<Contest> getContestInfo(Integer contestId) {
        System.out.println(contestId);
        Contest contest = contestService.getContestInfo(contestId);
        if(contest==null){
            return Result.error("不存在该比赛");
        }
        return Result.success(contest);
    }

    //通过比赛标题查询比赛Id
    @PostMapping("/findByTitle")
    public Result<Contest> findByTitle(String title) {
        System.out.println(title);
        Contest contest = contestService.findByTile(title);
        if(contest==null){
            return  Result.error("不存在该比赛");
        }
        return Result.success(contest);
    }

    //获得当前用户的比赛列表
    @GetMapping("/findByUserId")
    public Result findByUserId() {
        //获得当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        User user = userService.findById(userId);
//        System.out.println(user);

        List<ContestRecord> contestRecordList = contestRecordService.listByUserId(userId);

        List<Contest> contestList = new ArrayList<>();
        for (ContestRecord contestRecord : contestRecordList) {
            Contest contest = contestService.getContestInfo(contestRecord.getContestId());
            contestList.add(contest);
        }

//        System.out.println(contestList);
        return Result.success(contestList);
    }

}
