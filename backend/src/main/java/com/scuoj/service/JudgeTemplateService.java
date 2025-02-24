package com.scuoj.service;

import com.scuoj.pojo.JudgeTemplate;

import java.util.List;

public interface JudgeTemplateService {

    //新增评测模板
    void add(JudgeTemplate judgeTemplate);

    //删除评测模板
    void delete(Integer id);

    //通过模板ID查找评测模板
    JudgeTemplate getById(Integer id);

    //无条件获得评测模板列表
    List<JudgeTemplate> listUnConditional();

    //通过模板语言查找模板
    JudgeTemplate findByLanguage( String language );

    //更新评测模板
    void update( JudgeTemplate judgeTemplate );
}
