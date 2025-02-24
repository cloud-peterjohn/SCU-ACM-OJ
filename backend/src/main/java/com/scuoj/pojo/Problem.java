package com.scuoj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Problem {
    private Integer id;//主键ID
    @NotEmpty
    private String title;//题目标题
    private String problemUrl;//题目配置url
    private Integer timeLimit;//时间限制
    private Integer memoryLimit;//空间限制
    private Integer createUser;//创建人ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;//更新时间
    private boolean isFile = false;
    private String fileName = "null";
}
