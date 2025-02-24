package com.scuoj.controller;

import com.scuoj.pojo.*;
import com.scuoj.service.*;
import com.scuoj.utils.OjElemUtil;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/BangDan")
public class BangDanController {

    @Autowired
    JudgeRecordService judgeRecordService;

    @Autowired
    BelongService belongService;

    @Autowired
    ContestRecordService contestRecordService;

    @Autowired
    UserService userService;

    @Autowired
    ContestService contestService;

    //返回某比赛中本人所有题目的评测结果
    @GetMapping("/MyResultList")
    public Result<List<JudgeResult>> MyResultList( Integer contestId){
        //获取当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");

        return Result.success(JRByUserAndContest(loginId,contestId));
    }

    //获得该比赛下的所有成员的所有题目的评测结果
    @GetMapping("/AllResultList")
    public Result AllResultList(Integer contestId){
        //获取用户ID列表
        List<Integer> UserIdList = contestRecordService.userListHavingContest(contestId);
        //获得该比赛下的所有成员
        List<User> UserList = new ArrayList<>();
        for (Integer userId : UserIdList) {
            UserList.add(userService.findById(userId));
        }
        for(User user : UserList){
            user.setPassword("不告诉你");
        }
        //得到该比赛的信息
        Contest contest = contestService.getContestInfo(contestId);

        List<UserBangDan> BangDanList = new ArrayList<>();

        //遍历每一个人
        for (User user : UserList) {
            //得到这个人在这场比赛中的结果
            UserBangDan userBangDan = new UserBangDan();
            userBangDan.setJudgeResultList(JRByUserAndContest(user.getId(),contestId));
            userBangDan.setUserId(user.getId());
            userBangDan.setUserName(user.getUsername());

            //计算罚时
            Integer allTime = 0;
            //计算过题数目
            Integer allCnt = 0;
            for (JudgeResult judgeResult:userBangDan.getJudgeResultList()){
                if(judgeResult.getResult().equals("AC")){
                    allCnt++;
                    // 计算两个 LocalDateTime 之间的 Duration
                    Duration duration = Duration.between(contest.getStartTime(), judgeResult.getAcTime());
//                    System.out.println("contest.getStartTime()"+contest.getStartTime());
//                    System.out.println("judgeResult.getAcTime()"+judgeResult.getAcTime());
                    // 将 Duration 转换为分钟
                    Integer minutes = Math.toIntExact(duration.toMinutes());

//                    System.out.println("minutes"+minutes);

                    allTime+=minutes+ judgeResult.getCnt()*OjElemUtil.PenaltyRule;
                }
            }
            userBangDan.setAcNum(allCnt);
            userBangDan.setTimePenalty(allTime);
            userBangDan.setProblemNum(userBangDan.getJudgeResultList().size());

            //添加到返回值中
            BangDanList.add(userBangDan);
        }

        // 使用 Comparator 进行排序
        Collections.sort(BangDanList, new Comparator<UserBangDan>() {
            @Override
            public int compare(UserBangDan o1, UserBangDan o2) {
                // 首先按照 ACNum 降序排列
                int acNumCompare = o2.getAcNum().compareTo(o1.getAcNum());
                if (acNumCompare != 0) {
                    return acNumCompare;
                }
                // 如果 ACNum 相同，则按照 TimePenalty 升序排列
                return o1.getTimePenalty().compareTo(o2.getTimePenalty());
            }
        });

        int rank = 1;//排名
        for (UserBangDan userBangDan:BangDanList){
            userBangDan.setRank(rank);
            //更新比赛记录中的信息
            ContestRecord contestRecord = new ContestRecord();
            contestRecord.setContestId(contest.getId());
            contestRecord.setUserId(userBangDan.getUserId());
            contestRecord.setRanking(rank);
            contestRecord.setAcNum(userBangDan.getAcNum());
            contestRecord.setAllTime(userBangDan.getTimePenalty());

//            System.out.println(contestRecord.getAllTime());
            if(contestRecord.getAllTime()<0){
                contestRecord.setAllTime(0);
            }

            contestRecordService.update(contestRecord);

            rank++;
        }

        System.out.println(BangDanList);

        return Result.success(BangDanList);
    }

    public List<JudgeResult> JRByUserAndContest(Integer userId, Integer contestId){
        //获得该比赛
        Contest contest = contestService.getContestInfo(contestId);

        //获取该用户关于该比赛的所有评测记录
        List<JudgeRecord> jr = judgeRecordService.list(null,userId,null,null,contestId);

        //获得该比赛下的所有题目
        List<Problem> pb = belongService.listProblem(contestId);

        //最终返回的结果
        List<JudgeResult> judgeResultList = new ArrayList<>();

        for (Problem p : pb) {//循环比赛题目
            //获取当前题目的返回结果
            JudgeResult judgeResult = new JudgeResult();
            judgeResult.setTitle(p.getTitle());
            judgeResult.setProblemId(p.getId());
            judgeResult.setSubmitTime(contest.getStartTime());

            List<JudgeRecord> jrr = new ArrayList<JudgeRecord>();
            for (JudgeRecord j : jr) {//获得该题目的所有评测记录
                if(j.getProblemId().equals(p.getId())){
                    jrr.add(j);
                }
            }

            int cnt = 0;//AC前错误次数
            boolean flag = false;
            boolean IsAC = false;//是否AC
            //循环查找，直到AC
            for (JudgeRecord j : jrr) {
                if(j.getJudgeTime().isBefore(contest.getStartTime())){
                    continue;
                }
                if(j.getJudgeTime().isAfter(contest.getEndTime())){
                    continue;
                }
                judgeResult.setSubmitTime(j.getJudgeTime());
                if(j.getJudgeResult().equals("AC")){

//                    if(p.getTitle().equals("23423")){
//                        System.out.println(p.getTitle());
//                        System.out.println(userId);
//                    }

                    IsAC = true;
                    flag = false;
                    judgeResult.setAcTime(j.getJudgeTime());
                    judgeResult.setResult("AC");
                    break;
                }
//                System.out.println(j);
                if(!j.getJudgeResult().equals("CE")) cnt++;
                if(j.getJudgeResult().equals("CE")) flag = true;
            }
            if(IsAC){
                judgeResult.setResult("AC");
            }
            else if(cnt==0){
                judgeResult.setResult("Default");
            }
            else{
                judgeResult.setResult("WA");
            }
            if(flag){
                judgeResult.setResult("WA");
            }
            judgeResult.setCnt(cnt);

            if(!judgeResult.getResult().equals("WA")&&!judgeResult.getResult().equals("AC")){
                judgeResult.setResult("Default");
            }

            judgeResultList.add(judgeResult);

        }

        //根据题目ID排序
        Collections.sort(judgeResultList,new Comparator<JudgeResult>() {
            public int compare(JudgeResult o1, JudgeResult o2) {
                return o1.getProblemId().compareTo(o2.getProblemId());
            }
        });

        int num = 0;
        for (JudgeResult j : judgeResultList) {
            j.setNewId((char)('A'+num));
            num++;
        }

//        for (JudgeResult j:judgeResultList){
//            System.out.println(j);
//        }

        return judgeResultList;
    }
}
