package com.scuoj.mapper;

import com.scuoj.pojo.JudgeRecord;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface JudgeRecordMapper {

    @Insert("insert into judgerecord(problem_id,user_id,language,judge_time,judge_result,runtime,runmemory,code,compile_message,contest_id)" +
            "values(#{problemId},#{userId},#{language},#{judgeTime},#{judgeResult},#{runTime},#{runMemory},#{code},#{compileMessage},#{contestId})")
    void add(JudgeRecord judgeRecord);

//    @Insert("INSERT INTO judgerecord (" +
//            "problem_id, user_id, language, judge_time, " +
//            "judge_result, runtime, runmemory, code, " +
//            "compile_message, contest_id" +
//            ") VALUES (" +
//            "#{problemId}, #{userId}, #{language}, #{judgeTime}, " +
//            "#{judgeResult}, #{runTime}, #{runMemory}, #{code}, " +
//            "#{compileMessage}, #{contestId}" +
//            ") RETURNING id")
//    Integer add2(JudgeRecord judgeRecord);

    //分页查询
    List<JudgeRecord> list(Integer problemId, Integer userId, String language, String judgeResult,Integer contestId);

    @Update("update JudgeRecord set problem_id=#{problemId},user_id=#{userId},template_id=#{templateId},judge_time=#{judgeTime},judge_result=#{judgeResult},runtime=#{runTime},runmemory=#{runMemory},code=#{code},compile_message=#{compileMessage} where id=#{id}")
    void update(JudgeRecord judgeRecord);

    @Delete("delete from judgeRecord where user_id=#{userId}")
    void deleteByUserId( Integer userId );

    @Delete("delete from judgeRecord where contest_id=#{contestId}")
    void deleteByContestId( Integer contestId );
}
