package com.scuoj.controller;

import com.scuoj.pojo.JudgeTemplate;
import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import com.scuoj.service.JudgeTemplateService;
import com.scuoj.service.UserService;
import com.scuoj.utils.OjElemUtil;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/JudgeTemplate")
//@CrossOrigin(origins = "http://localhost:8080")
public class JudgeTemplateController {

    @Autowired
    private JudgeTemplateService judgeTemplateService;

    @Autowired
    private UserService userService;

    //通过模板ID查找评测模板
    @GetMapping("/getById")
    public JudgeTemplate getById(Integer id) {
        return judgeTemplateService.getById(id);
    }

    //通过模板语言查找评测模板
    @GetMapping("/findByLanguage")
    public JudgeTemplate findByLanguage(String language) {
        return judgeTemplateService.findByLanguage(language);
    }

    //新增评测模板
    @PostMapping("/add")
    public Result add(@RequestBody @Validated JudgeTemplate judgeTemplate) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }

        System.out.println(judgeTemplate);

        judgeTemplateService.add(judgeTemplate);
        return Result.success();
    }

    @DeleteMapping("/delete")
    public Result delete(Integer id) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }

        if(judgeTemplateService.getById(id)==null){
            return Result.error("模板不存在");
        }

        judgeTemplateService.delete(id);
        return Result.success();
    }

    @DeleteMapping("/deleteList")
    public Result deleteList(@RequestBody List<Integer> idList) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }

        for(Integer id : idList){
            judgeTemplateService.delete(id);
        }
        return Result.success();
    }

    //通过模板语言删除模板
    @DeleteMapping("/deleteByLanguage")
    public Result deleteByLanguage(@RequestBody String language) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }
        JudgeTemplate judgeTemplate = judgeTemplateService.findByLanguage(language);
        if(judgeTemplate==null){
            return Result.error("模板不存在");
        }
        judgeTemplateService.delete(judgeTemplate.getId());
        return Result.success("删除成功");
    }

    //通过模板语言删除模板列表
    @DeleteMapping("/deleteListByLanguage")
    public Result deleteListByLanguage(List<String> languageList) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }

        for(String language : languageList){
            JudgeTemplate judgeTemplate = judgeTemplateService.findByLanguage(language);
            judgeTemplateService.delete(judgeTemplate.getId());
        }
        return Result.success();
    }

    //更新评测模板
    @PutMapping("/update")
    public Result update(@RequestBody @Validated JudgeTemplate judgeTemplate) {
        //查询当前登录的人ID
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if(OjElemUtil.map.get(loginUser.getAuth())!=1){
            return Result.error("权限不足");
        }
        judgeTemplateService.update(judgeTemplate);
        return Result.success();
    }

    //获取评测模板列表
    @GetMapping("/listUnConditional")
    public Result listUnConditional() {
        List<JudgeTemplate> jt = judgeTemplateService.listUnConditional();
        return Result.success(jt);
    }

}
