package com.scuoj.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.scuoj.pojo.PageBean;
import com.scuoj.pojo.Problem;
import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import com.scuoj.service.BelongService;
import com.scuoj.service.ProblemService;
import com.scuoj.service.UserService;
import com.scuoj.utils.OjElemUtil;
import com.scuoj.utils.ThreadLocalUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.eclipse.angus.mail.imap.protocol.ListInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/problem")
//@CrossOrigin(origins = "http://localhost:8080")
public class ProblemController {

    @Autowired
    private ProblemService problemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BelongService belongService;

    //添加题目
    @PostMapping("/add")
    public Result add(@RequestBody @Validated Problem problem) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }
        System.out.println(problem);
        problemService.add(problem);

        return Result.success();
    }

    @GetMapping("/pageList")
    public Result<PageBean<Problem>> pageList(
        Integer pageNum,
        Integer pageSize,
        @RequestParam(required = false) Integer id,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) Integer createUser
    ){
        PageBean<Problem> pb = problemService.pageList(pageNum,pageSize,id,title,createUser);
        return Result.success(pb);
    }

    //获取题目信息
    @GetMapping("/problemInfo")
    public Result getProblemInfo(Integer id){
        Problem problem = problemService.getProblemInfo(id);
        if(problem==null){
            return Result.error("不存在该题目");
        }
        return Result.success(problem);
    }

    //删除题目
    @DeleteMapping("/delete")
    public Result delete(Integer id) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        problemService.delete(id);
        return Result.success();
    }

    //删除题目列表
    @DeleteMapping("/deleteList")
    public Result deleteList(@RequestBody List<Integer> IdList){
        for (Integer id : IdList) {
            problemService.delete(id);
        }
        return Result.success();
    }

    //通过题目标题获取题目
    @GetMapping("/findByTitle")
    public Result findByTitle(String title) {
        Problem problem = problemService.findByTitle(title);
        if(problem==null){
            return Result.error("不存在该题目");
        }
        return Result.success(problem);
    }

    //通过题目标题获取题目
    @PostMapping("/findByTitle2")
    public Result findByTitle2(String prob_title) {
        Problem problem = problemService.findByTitle(prob_title);
        if(problem==null){
            return Result.error("不存在该题目");
        }
        return Result.success(problem);
    }

    //获取题目列表
    @GetMapping("/list")
    public Result<List<Problem>> list(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer createUser
    ) {
        return Result.success(problemService.list(id,title,createUser));
    }

    //更新题目信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated Problem problem) {
        //查询当前登录用户
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        problemService.update(problem);
        return Result.success();
    }

    // 上传题目文件
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file, @RequestParam("problemId") Integer problemId) throws IOException {
        // 查询当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        // 判断权限
        if ("user".equals(loginUser.getAuth())) {
            return Result.error("权限不足");
        }

        // 将文件的内容存储到本地磁盘上
        String originalFilename = file.getOriginalFilename();
        String rootPath = OjElemUtil.rootPath + "/" + problemId; // 使用 Linux 路径分隔符

        String sor = rootPath + "/" + originalFilename; // 使用 Linux 路径分隔符
        String newsor = sor.substring(0, sor.length() - 4); // 去掉文件扩展名

        // 创建目录
        File directory = new File(newsor);
        if (!directory.mkdirs() && !directory.isDirectory()) {
            return Result.error("文件夹创建失败");
        }

        // 上传文件
        file.transferTo(new File(sor));

        // 如果是压缩包就解压
        if (sor.endsWith(".zip")) {
            OjElemUtil.unzip(sor, newsor);
        }

        // 编译check.cpp
        String compileCheckCmd = "g++ -o " + newsor + "/check " + newsor + "/files/check.cpp";
        try {
            int exitCode = executeCommand(compileCheckCmd);
            if (exitCode != 0) {
                return Result.error("编译失败，退出码：" + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编译过程中发生错误");
        }

        // 更新题目信息
        Problem problem = problemService.getProblemInfo(problemId);
        problem.setProblemUrl(newsor); // 使用解压后的目录路径
        problem.setFile(true);
        problemService.update(problem);

        return Result.success(newsor);
    }

    private int executeCommand(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        return process.waitFor();
    }

    //获取未绑定比赛的题目列表
    @GetMapping("/listNoBelong")
    public Result<List<Problem>> listNoBelong(){
        //获得所有题目
        List<Problem> AllProblem = problemService.list(null,null,null);
        System.out.println(AllProblem);
        //获得已绑定比赛的题目列表
        List<Problem> BelongProblem = belongService.allProblem();
        System.out.println(BelongProblem);

        AllProblem.removeAll(BelongProblem);
        return Result.success(AllProblem);
    }

    //下载题目题面
    @PostMapping("/downloadProblem")
    public Result downloadProblem(Integer problemId) throws IOException {
        Problem problem = problemService.getProblemInfo(problemId);
        if(!problem.isFile()){
            return Result.error("该文件无题目配置");
        }
        String fileUrl = problem.getProblemUrl()+"\\"+"statements\\.pdf\\chinese\\problem.pdf";

        try (PDDocument document = PDDocument.load(new File(fileUrl))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return Result.success(stripper.getText(document));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件读取失败");
        }
    }

    //下载pdf文件
    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadFile(String problemTitle) throws FileNotFoundException {
//        System.out.println(problemTitle);

        Problem problem = problemService.findByTitle(problemTitle);

        File file = new File(problem.getProblemUrl() + "\\statements\\.pdf\\chinese\\problem.pdf");

//        System.out.println(file);

        if (!file.exists()) {
            throw new FileNotFoundException("没有发现该文件");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

//        System.out.println(file.getName());

        System.out.println(resource);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(file.length())
                .body(resource);
    }
}
