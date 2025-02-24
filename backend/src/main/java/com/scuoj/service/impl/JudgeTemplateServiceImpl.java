package com.scuoj.service.impl;

import com.scuoj.mapper.JudgeRecordMapper;
import com.scuoj.mapper.JudgeTemplateMapper;
import com.scuoj.pojo.JudgeTemplate;
import com.scuoj.service.JudgeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgeTemplateServiceImpl implements JudgeTemplateService {

    @Autowired
    private JudgeTemplateMapper judgeTemplateMapper;

    @Override
    public void add(JudgeTemplate judgeTemplate) {
        judgeTemplateMapper.add(judgeTemplate);
    }

    @Override
    public void delete(Integer id) {
        judgeTemplateMapper.delete(id);
    }

    @Override
    public JudgeTemplate getById(Integer id) {
        return judgeTemplateMapper.getById(id);
    }

    @Override
    public List<JudgeTemplate> listUnConditional() {
        return judgeTemplateMapper.listUnConditional();
    }

    @Override
    public JudgeTemplate findByLanguage( String language ) {
        return judgeTemplateMapper.findByLanguage(language);
    }

    @Override
    public void update( JudgeTemplate judgeTemplate ) {
        judgeTemplateMapper.update(judgeTemplate);
    }
}
