package com.scuoj.mapper;

import com.scuoj.pojo.Problem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProblemMapper {

    @Insert("insert into problem(title,problem_url,create_user,create_time,update_time,memory_limit,time_limit,file_name)" +
            "values(#{title},#{problemUrl},#{createUser},#{createTime},#{updateTime},#{memoryLimit},#{timeLimit},#{fileName})")
    void add(Problem problem);

    //分页，查询符合条件的题目
    List<Problem> list(Integer id, String title, Integer createUser);

    @Delete("delete from problem where id=#{id}")
    void delete(Integer id);

    @Select("select * from problem where id=#{id}")
    Problem getProblemInfo(Integer id);

    @Select("select * from problem where title=#{title}")
    Problem findByTitle(String title);

    @Update("update problem set title=#{title},problem_url=#{problemUrl},update_time=#{updateTime},memory_limit=#{memoryLimit},time_limit=#{timeLimit},is_file=#{isFile},file_name=#{fileName} where id=#{id}")
    void update(Problem problem);
}
