package com.scuoj.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JudgeTemplate {
    private Integer id;//评测模板ID
    @NotNull
    private String language;//评测模板语言
    private String description;
    private String comment;
}
