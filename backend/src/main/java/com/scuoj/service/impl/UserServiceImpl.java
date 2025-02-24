package com.scuoj.service.impl;

import com.scuoj.mapper.UserMapper;
import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import com.scuoj.service.UserService;
import com.scuoj.utils.Md5Util;
import com.scuoj.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.scuoj.utils.Md5Util.getMD5String;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String email, String username, String password) {
        //加密
        String md5String = Md5Util.getMD5String(password);
        //添加
        userMapper.register(email, username,md5String);
    }

    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        if(user.getId()==null){
            Map<String, Object> map = ThreadLocalUtil.get();
            user.setId((Integer) map.get("id"));
        }
        userMapper.update(user);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public void updatePwd(String newPwd) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updatePwd(Md5Util.getMD5String(newPwd),id);
    }

    @Override
    public void updatePwdByEmail(String email, String newPwd) {
        userMapper.updatePwdByEmail(Md5Util.getMD5String(newPwd),email);
    }

    @Override
    public void updateAuth(String username) {
        userMapper.updateAuth(username,"Admin");
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public User findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public void add(User user) {
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        //加密
        String md5String = Md5Util.getMD5String(user.getPassword());
        user.setPassword(md5String);
        userMapper.add(user);
    }

    @Override
    public List<User> list() {
        return userMapper.list();
    }

    @Override
    public void deleteById(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public void deleteByUsername( String username ) {
        userMapper.deleteByUsername(username);
    }

    @Override
    public void updateUser( User user ) {
        userMapper.updateUser(user);
    }

    @Override
    public void updateUserPwd( User user ) {
        String md5String = Md5Util.getMD5String(user.getPassword());
        user.setPassword(md5String);
        userMapper.updateUserPwd(user);
    }

    @Override
    public List<User> findByAuth( String auth ) {
        return userMapper.findByAuth(auth);
    }

    @Override
    public void updateMyInfo( User user ) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateMyInfo(user);
    }

}
