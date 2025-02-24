package com.scuoj.mapper;

import com.scuoj.pojo.JudgeTemplate;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JudgeTemplateMapper {

    @Insert("insert into judgetemplate(language,description,comment)" +
            "values(#{language},#{description},#{comment})")
    void add(JudgeTemplate judgeTemplate);

    @Delete("delete from judgetemplate where id=#{id}")
    void delete(Integer id);

    @Select("select * from judgetemplate where id=#{id}")
    JudgeTemplate getById(Integer id);

    @Select("select * from judgetemplate")
    List<JudgeTemplate> listUnConditional();

    @Select("select * from judgetemplate where language=#{language}")
    JudgeTemplate findByLanguage( String language );

    @Update("update judgetemplate set language=#{language},description=#{description}, comment=#{comment} where id=#{id}")
    void update( JudgeTemplate judgeTemplate );
}
