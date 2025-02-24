package com.scuoj.controller;

import com.scuoj.pojo.Problem;
import com.scuoj.pojo.Result;
import com.scuoj.service.ProblemService;
import com.scuoj.utils.OjElemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api")
public class FileUploadController {

    @Autowired
    private ProblemService problemService;

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        // 将文件的内容存储到本地磁盘上
        String originalFilename = file.getOriginalFilename();
        String newFileName = "scuoj/" + originalFilename; // 修改为Linux路径
        File destFile = new File(OjElemUtil.rootPath + newFileName);
        file.transferTo(destFile);
        String sor = OjElemUtil.rootPath + newFileName;

        // 如果是压缩包就解压
        if (sor.endsWith(".zip")) {
            unzip(sor, OjElemUtil.rootPath + "scuoj");
        }

        return Result.success("url访问地址...");
    }

    // 解压ZIP文件到指定目录
    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs(); // 如果目录不存在，则创建目录
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // 遍历ZIP内的条目
        while (entry != null) {
            String entryName = entry.getName();
            // 将 Windows 路径转换为 Linux 路径
            entryName = entryName.replace("\\", "/");
            String filePath = destDirectory + File.separator + entryName;

            if (!entry.isDirectory()) {
                // 如果是文件，则解压
                extractFile(zipIn, filePath);
            } else {
                // 如果是目录，则创建目录
                File dir = new File(filePath);
                dir.mkdirs();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        // 删除原始ZIP文件
        File zipFile = new File(zipFilePath);
        zipFile.delete();
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> exportPdf(Integer problemId) {
        try {
            Problem problem = problemService.getProblemInfo(problemId);
            if (problem == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String filePath = problem.getProblemUrl() + "/statements/.pdf/chinese/problem.pdf"; // 修改为Linux路径
            Path path = Paths.get(filePath);

            if (!Files.exists(path)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            // 读取PDF文件
            byte[] pdfBytes = Files.readAllBytes(path);

            // 获取文件名（假设从problem对象中获取中文标题）
            String filename = problem.getTitle() + "_" + problemId + ".pdf";

            // 设置响应头，确保中文文件名正确编码
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);

            // 使用UTF-8编码文件名
            ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                    .filename(filename, StandardCharsets.UTF_8)
                    .build();

            headers.setContentDisposition(contentDisposition);
            headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 从ZIP输入流中提取文件
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File outFile = new File(filePath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs(); // 创建父目录
        }
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }
}