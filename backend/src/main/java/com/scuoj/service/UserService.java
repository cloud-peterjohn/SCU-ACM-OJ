package com.scuoj.service;

import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public interface UserService {
    //根据用户名查询用户
    User findByUserName(String username);

    //注册
    void register(String email, String username, String password);

    //更新用户信息
    void update(User user);

    //更新用户头像
    void updateAvatar(String avatarUrl);

    //更新用户密码
    void updatePwd(String newPwd);

    // 通过邮箱更新密码
    void updatePwdByEmail(String email, String newPwd);

    //更新用户权限
    void updateAuth(String username);

    //通过邮箱查找用户
    User findByEmail(String email);

    //通过Id查找用户
    User findById(Integer id);

    //管理员添加用户
    void add(User user);

    //无条件返回用户列表
    List<User> list();

    //通过Id删除
    void deleteById(Integer id);

    //通过用户名删除
    void deleteByUsername( String username );

    //更新用户信息
    void updateUser( User user );

    //只更新用户密码
    void updateUserPwd( User user );

    //通过权限查找用户
    List<User> findByAuth( String auth );

    //更改自己的信息
    void updateMyInfo( User user );
}
