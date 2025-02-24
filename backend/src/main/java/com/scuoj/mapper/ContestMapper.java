package com.scuoj.mapper;

import com.scuoj.pojo.Contest;
import com.scuoj.pojo.Problem;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContestMapper {

    @Insert("insert into contest(title,create_user,start_time,end_time,create_time,update_time)" +
            "values(#{title},#{createUser},#{startTime},#{endTime},#{createTime},#{updateTime})")
    void add(Contest contest);

    @Update("update contest set title=#{title},start_time=#{startTime},end_time=#{endTime},update_time=#{updateTime} where id=#{id}")
    void update(Contest contest);

    //分页查询
    List<Contest> list(Integer id, String title, Integer createUser);

    @Delete("delete from contest where id=#{id}")
    void delete(Integer id);

    @Select("select * from contest where id=#{id}")
    Contest getContestInfo(Integer id);

    @Select("select * from contest where title=#{title}")
    Contest findByTitle(String title);
}
