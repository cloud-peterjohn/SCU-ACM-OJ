package com.scuoj.mapper;

import com.scuoj.pojo.ContestRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ContestRecordMapper {

    @Insert("insert into contestrecord(contest_id,user_id,ranking,acnum,alltime)" +
            "values(#{contestId},#{userId},#{ranking},#{acNum},#{allTime})")
    void add(ContestRecord contestRecord);

    @Update("update contestrecord set ranking=#{ranking},acnum=#{acNum},alltime=#{allTime} where contest_id=#{contestId} and user_id=#{userId}")
    void update(ContestRecord contestRecord);

    //分页列表查询
    List<ContestRecord> list(Integer contestId, Integer userId);

    //通过用户ID删除比赛记录
    @Delete("delete from contestrecord where user_id=#{userId}")
    void deleteByUserId( Integer userId );

    //获取已经添加至比赛的用户列表
    @Select("select distinct user_id from contestrecord where contest_id=#{contestId}")
    List<Integer> userListHavingContest(Integer contestId);

    //找到当前用户的比赛
    @Select("select * from contestrecord where user_id=#{userId}")
    List<ContestRecord> listByUserId(Integer userId);
}
