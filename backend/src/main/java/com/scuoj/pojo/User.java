package com.scuoj.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;//主键ID
    private String username;//用户名
    private String password;//密码
    private String nickname;//昵称
    @NotEmpty
    @Email
    private String email;//邮箱
    private String userPic;//用户头像地址
    private String gender;//用户性别
    private String auth;//权限，superAdmin:超级管理员 Admin:管理员 user:用户
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;//更新时间

    private String token;

    public Integer getId() {
        return id;
    }

    public void setId( Integer id ) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public @NotEmpty @Email String getEmail() {
        return email;
    }

    public void setEmail( @NotEmpty @Email String email ) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname( String nickname ) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender( String gender ) {
        this.gender = gender;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth( String auth ) {
        this.auth = auth;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime( LocalDateTime createTime ) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime( LocalDateTime updateTime ) {
        this.updateTime = updateTime;
    }
}
