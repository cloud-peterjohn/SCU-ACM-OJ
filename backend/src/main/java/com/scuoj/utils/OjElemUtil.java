package com.scuoj.utils;

import com.scuoj.pojo.User;
import com.scuoj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class OjElemUtil {
    public static String rootPath = System.getProperty("user.dir")+"//problem";

    public static Integer fileNum = 0;

    public static Integer PenaltyRule = 20;

    public static final Map<String,Integer> map = new HashMap<String,Integer>();
    static {
        map.put("superAdmin", 1);
        map.put("admin", 2);
        map.put("user", 3);
    }

    public static final Map<String,Integer> languageToId = new HashMap<String, Integer>();
    static {
        languageToId.put("C/C++", 1);
        languageToId.put("Java", 2);
        languageToId.put("Python", 3);
    }

    public static final Map<Integer,String> idToLanguage = new HashMap<Integer,String>();
    static {
        idToLanguage.put(1, "C/C++");
        idToLanguage.put(2, "Java");
        idToLanguage.put(3, "Python");
    }

    // 解压ZIP文件到指定目录
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir(); // 如果目录不存在，则创建目录
        }
        System.out.println(destDir);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // 遍历ZIP内的条目
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // 如果是文件，则解压
                extractFile(zipIn, filePath);
            } else {
                // 如果是目录，则创建目录
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        // 删除原始ZIP文件
        File zipFile = new File(zipFilePath);
        zipFile.delete();
    }

    // 从ZIP输入流中提取文件
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

}
