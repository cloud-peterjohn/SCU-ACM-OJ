package com.scuoj.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class CommandUtil {
    /**
     * 运行外部命令并捕获输出和错误。
     *
     * @param cmd       要执行的命令
     * @param inputFilePath 输入文件路径
     * @param stdoutFile 标准输出文件路径
     * @param stderrFile 标准错误文件路径
     * @return 子进程的退出代码
     */
    public static int run(String cmd, String inputFilePath, String stdoutFile, String stderrFile,Integer time_limit) {
        if(time_limit == null) time_limit = 10000;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(cmd.split(" "));
            // 重定向错误流到文件
            if(stderrFile != null) {
                File errorFile = new File(stderrFile);
                processBuilder.redirectError(errorFile);
            }
            // 重定向输出流到文件
            if (stdoutFile != null) {
                File outputfile = new File(stdoutFile);
                processBuilder.redirectOutput(outputfile);
            }
            // 设置输入文件
            if(inputFilePath!=null) {
                processBuilder.redirectInput(new File(inputFilePath));
            }

            Process process = processBuilder.start();
            try {
                boolean finished = process.waitFor(time_limit, TimeUnit.MILLISECONDS); //
                if (!finished) {
                    process.destroyForcibly(); // 超时后强制终止进程
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 等待子进程结束
            int exitCode = process.waitFor();
            //System.out.println("Exit code: " + exitCode);
            return exitCode;
        }   catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return 2; // 发生IO异常或中断异常，视为运行时错误
        }
    }
}