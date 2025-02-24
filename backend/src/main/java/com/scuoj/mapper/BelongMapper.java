package com.scuoj.mapper;

import com.scuoj.pojo.Belong;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BelongMapper {
    //添加一条题目归属表
    @Insert("insert into belong(problem_id,contest_id)" +
            "values(#{problemId},#{contestId})")
    void add(Integer problemId, Integer contestId);

    //通过题目Id查找对应比赛
    @Select("select contest_id from belong where problem_id=#{problemId}")
    Integer findByProblemId(Integer problemId);

    //通过比赛Id查找对应的题目
    @Select("select distinct problem_id from belong where contest_id=#{contestId}")
    List<Integer> list(Integer contestId);

    //通过所有Id查找唯一得归属表
    @Select("select * from belong where problem_id=#{problemId} and contest_id=#{contestId}")
    Belong getById(Integer problemId, Integer contestId);

    @Delete("delete from belong where problem_id=#{problemId}")
    void delete(Integer problemId);

    @Select("select distinct problem_id from belong")
    List<Integer> allproblem();
}
