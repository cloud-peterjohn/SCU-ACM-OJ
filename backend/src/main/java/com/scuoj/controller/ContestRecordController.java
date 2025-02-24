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
@RequestMapping("/api/contestRecord")
//@CrossOrigin(origins = "http://localhost:8080")
public class ContestRecordController {

    @Autowired
    ContestRecordService contestRecordService;

    @Autowired
    UserService userService;

    @Autowired
    ContestService contestService;

    //新增比赛记录
    @PostMapping("/add")
    public Result add(@RequestBody @Validated ContestRecord contestRecord) {
        contestRecordService.add(contestRecord);
        return Result.success();
    }

    //更新比赛记录
    @PutMapping("/update")
    public Result update(@RequestBody @Validated ContestRecord contestRecord) {
        contestRecordService.update(contestRecord);
        return Result.success();
    }

    //列表
    @GetMapping("/pageList")
    public Result<PageBean<ContestRecord>> pageList(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer contestId,
            @RequestParam(required = false) Integer userId
    ){
        PageBean<ContestRecord> pb = contestRecordService.pageList(pageNum,pageSize,contestId,userId);
        return Result.success(pb);
    }

    //获取比赛记录列表
    @GetMapping("/list")
    public Result<List<ContestRecord>> list(
            @RequestParam(required = false) Integer contestId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String username
    ){
        if(username!=null){
            User u = userService.findByUserName(username);
            userId = u.getId();
        }
        List<ContestRecord> pb = contestRecordService.list(contestId,userId);
        return Result.success(pb);
    }

    //通过用户ID删除比赛记录
    @DeleteMapping("/deleteByUserId")
    public Result deleteByUserId(@RequestBody Integer userId){
        contestRecordService.deleteByUserId(userId);
        return Result.success();
    }

    //通过用户ID删除比赛记录列表
    @DeleteMapping("/deleteListByUserId")
    public Result deleteListByUserId(@RequestBody List<Integer> userIdList){
        for (Integer userId : userIdList) {
            contestRecordService.deleteByUserId(userId);
        }
        return Result.success();
    }

    //获取已经参加比赛的用户列表
    @GetMapping("/userListHavingContest")
    public Result userListHavingContest( Integer contestId){
        System.out.println(contestId);
        //获取用户ID列表
        List<Integer> UserIdList = contestRecordService.userListHavingContest(contestId);
        //将用户ID列表转换为用户列表
        List<User> UserList = new ArrayList<>();
        for (Integer UserId : UserIdList) {
            UserList.add(userService.findById(UserId));
        }
        for(User user : UserList){
            user.setPassword("不告诉你");
        }
        return Result.success(UserList);
    }

    //获得某比赛用户的比赛记录
    @GetMapping("/listByUserId")
    public Result<List<ContestRecord>> listByUserId(){
        //获得当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("userId");
        User user = userService.findById(userId);

        List<ContestRecord> contestRecordList = contestRecordService.listByUserId(userId);

        return Result.success(contestRecordList);
    }

    //以列表的形式为一场比赛添加用户
    @PostMapping("/addByUserIdList")
    public Result addByUserIdList(@RequestBody List<String> userNameList){
        //获得当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer userIdd = (Integer) map.get("id");
        User user = userService.findById(userIdd);
        if(user.getAuth().equals("user")) return Result.error("权限不足");

//        System.out.println(userIdList);

        //列表首位为比赛标题
        String contestTitle = userNameList.get(0);
        userNameList.remove(0);
        System.out.println(contestTitle);
        Contest contest = contestService.findByTile(contestTitle);

        for(String username : userNameList){
            User u = userService.findByUserName(username);
            if(u==null) continue;

            //查看是否已存在一个比赛记录
            List<ContestRecord> crList = contestRecordService.list(contest.getId(),u.getId());
            if(!crList.isEmpty()) continue;

            if(u.getAuth().equals("user")){
                ContestRecord contestRecord = new ContestRecord();
                contestRecord.setContestId(contest.getId());
                contestRecord.setUserId(u.getId());
                contestRecordService.add(contestRecord);
            }
        }
        return Result.success();
    }

//    //更新比赛记录中的排名，AC数量，总罚时等信息
//    @PostMapping("/updateACRankALLtime")
//    public Result updateACRankALLtime(@RequestBody ContestRecord contestRecord){
//        contestRecordService.update
//    }

//    //获得比赛中已经存在的用户
//    @GetMapping("/findUserByContestId")
//    public Result findUserByContestId(@RequestParam Integer contestId){
//        return Result.success(contestRecordService.findUserByContestId(contestId));
//    }
}
