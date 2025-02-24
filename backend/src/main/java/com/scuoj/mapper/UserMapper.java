package com.scuoj.mapper;

import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    //根据用户名查询用户
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //添加
    @Select("insert into user(email,username,password,create_time,update_time)"+
        "values(#{email},#{username},#{password},now(),now())")
    void register(String email, String username, String password);

    @Update("update user set username=#{username},nickname=#{nickname},email=#{email},gender=#{gender},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set username=#{username},nickname=#{nickname},email=#{email},gender=#{gender},update_time=#{updateTime} where id=#{id}")
    void updateMyInfo(User user);

    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    void updateAvatar(String avatarUrl, Integer id);

    @Update("update user set password=#{md5String},update_time=now() where id=#{id}")
    void updatePwd(String md5String, Integer id);

    @Update("update user set password=#{newPwd},update_time=now() where email=#{email}")
    void updatePwdByEmail(String newPwd, String email);

    @Update("update user set auth=#{Admin},update_time=now() where username=#{username}")
    void updateAuth(String username,String Admin);

    @Select("select * from user where email=#{email}")
    User findByEmail(String email);

    @Select("select * from user where id=#{id}")
    User findById(Integer id);

    @Insert("insert into user(username,password,nickname,email,gender,auth,create_time,update_time)" +
            "values(#{username},#{password},#{nickname},#{email},#{gender},#{auth},#{createTime},#{updateTime})")
    void add(User user);

    @Select("select * from user where auth!='superAdmin'")
    List<User> list();

    @Delete("delete from user where id=#{id}")
    void deleteById( Integer id );

    @Delete("delete from user where username=#{username}")
    void deleteByUsername(String username);

    @Update("update user set username=#{username},nickname=#{nickname},email=#{email},gender=#{gender},auth=#{auth},update_time=now() where id=#{id}")
    void updateUser( User user );

    @Update("update user set password=#{password} where id=#{id}")
    void updateUserPwd( User user );

    @Select("select * from user where auth=#{auth}")
    List<User> findByAuth( String auth );
}
