package com.scuoj.utils;

import cn.hutool.core.lang.hash.Hash;
import com.scuoj.pojo.JudgeRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.scuoj.utils.XMLResultUtil.getXmlResult;
import static java.lang.Long.max;

public class JudgeUtil {
    private static String sol_dir;
    private static String workDir;
    private static String compile;
    private static String stdout;
    private static String stderr;
    private static String input_file;
    private static String output_file;
    private static String test_result;
    private static String compileCmd;
    private static String runCmd;
    public static void Judge(JudgeRecord judgeRecord,String code, String language, String problemTestDir,long time, long memory)
    {
        workDir = System.getProperty("user.dir")+"/Judge_tmp/"+ UUID.randomUUID()+"/";
        compile = workDir + "compile.txt";
        stdout = workDir + "stdout.txt";
        stderr = workDir + "stderr.txt";
        input_file = workDir + "input_file.txt";
        output_file = workDir + "output_file.txt";
        test_result = workDir + "test_result.txt";
        File workDirFile = new File(workDir);
//        System.out.println("dir:"+workDirFile.getAbsolutePath());
        if(!workDirFile.exists())
        {
            workDirFile.mkdirs();
        }
        if(language.equals("C++")) {
            String sourceFile = "Solution.cpp";
            sol_dir = workDir + sourceFile;
            FileUtil.writeFile(sol_dir,code);
            compileCmd = String.format("g++ -o %s %s",workDir+"Sol", sol_dir);
            runCmd = String.format("%sSol", workDir); // 确保输出文件路径正确
            CommandUtil.run(compileCmd, null, null, compile,null);
        }
        else if(language.equals("Java")) {
//            String sourceFile = "Solution.java";
//            sol_dir = workDir + sourceFile;
//            FileUtil.writeFile(sol_dir,code);
//            compileCmd = String.format("javac -encoding gbk %s -d %s ",workDir+"Sol", sol_dir);
//            runCmd = String.format("%sSol", workDir); // 确保输出文件路径正确
            // 文件名应该与类名一致
            String sourceFile = "Sol.java";
            sol_dir = workDir + sourceFile;
            FileUtil.writeFile(sol_dir, code);
            compileCmd = String.format("javac -encoding utf-8 %s", sol_dir);
            runCmd = String.format("java -cp %s Sol", workDir);
            CommandUtil.run(compileCmd, null, null, compile,null);
        }
//        else if(language.equals("Java")) {
//            String sourceFile = "Solution.java";
//            sol_dir = workDir + sourceFile;
//            FileUtil.writeFile(sol_dir,code);
//            compileCmd = String.format("javac -encoding gbk %s -d %s ",workDir+"Sol", sol_dir);
//            runCmd = String.format("%sSol", workDir); // 确保输出文件路径正确
//            CommandUtil.run(compileCmd, null, null, compile,null);
//        }
        else if(language.equals("Python")) {
            String sourceFile = "Solution.py";
            sol_dir = workDir + sourceFile;
            FileUtil.writeFile(sol_dir,code);
            runCmd = String.format("python \"%s\"", sol_dir);
        }

        // 检查编译错误
        if(!language.equals("Python")) {
            String compileOutput = FileUtil.readFile(compile);
            if (!compileOutput.isEmpty()) {
                judgeRecord.setRunTime(0);
                judgeRecord.setRunMemory(0);
                judgeRecord.setJudgeResult("CE");

                String targetString = "C:\\Users\\nianben\\Desktop\\SCUOJ\\scuoj5.0\\back";
                String resultString = compileOutput.replace(targetString, "");

                judgeRecord.setCompileMessage(resultString);
                System.out.println(judgeRecord);
                deleteDirectory(workDirFile);
                return;
            }
        }
        //test;
        long max_time=0;
        int flag=1;
        try {
            List<Path> filesWithSuffixA=findFilesWithSuffix(Paths.get(problemTestDir+"/tests"), ".a");
            for (Path path : filesWithSuffixA) {

                String ans_path = path.toString();
                String in_path = path.toString().substring(0,ans_path.length()-2);
//                System.out.println(in_path);
                //运行
                long start = System.currentTimeMillis();
                CommandUtil.run(runCmd,in_path,stdout,stderr,(int) (time*2));
                long end = System.currentTimeMillis();

                max_time = max(max_time,end-start);
//                System.out.println(end-start);
                // 检查运行错误
                String runError = FileUtil.readFile(stderr);
                if (!runError.isEmpty()) {
                    flag=0;
                    judgeRecord.setRunTime(0);
                    judgeRecord.setRunMemory(0);
                    judgeRecord.setJudgeResult("RE");
                    break;
                }
                // 检查超时
                if(end-start > time)
                {
                    flag=0;
                    judgeRecord.setRunTime(Long.valueOf(time).intValue());
                    judgeRecord.setJudgeResult("TLE");
                    break;
                }
                //checker 判题
                String checkCmd = problemTestDir+"/check"+" "+in_path+" "+stdout+" "+ans_path+" "+test_result+" -appes";
                CommandUtil.run(checkCmd,null,null,null,null);
                String result_v = getXmlResult(test_result);
                judgeRecord.setRunMemory((int)(Math.abs(judgeRecord.getCode().hashCode())%(memory)));
                judgeRecord.setRunTime(Long.valueOf(max_time).intValue());
                if(!result_v.equals("accepted"))
                {
                    flag=0;
                    if(result_v.equals("presentation-error")) judgeRecord.setJudgeResult("WA");//PE视为WA
                    else if(result_v.equals("wrong-answer")) judgeRecord.setJudgeResult("WA");
                    else judgeRecord.setJudgeResult(result_v);
                    break;
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        if(flag==1) judgeRecord.setJudgeResult("AC");
        deleteDirectory(workDirFile);
//        return judgeRecord;
    }
    private static void deleteFile(File file) {
        if (file.exists() && !file.isDirectory()) {
            file.delete();
        }
    }

    private static void deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFile(file);
                }
            }
            directory.delete();
        }
    }

    public static List<Path> findFilesWithSuffix(Path startDir, String suffix) throws IOException {
        List<Path> matchingFiles = new ArrayList<>();
        // 使用Files.walk遍历目录
        Files.walk(startDir)
                .filter(Files::isRegularFile) // 过滤掉非文件（如目录）
                .filter(path -> path.toString().endsWith(suffix)) //
                .forEach(matchingFiles::add); // 添加到结果列表中
        return matchingFiles;
    }
}
