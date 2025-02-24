package com.scuoj.controller;

import com.scuoj.pojo.Result;
import com.scuoj.pojo.User;
import com.scuoj.service.EmailService;
import com.scuoj.service.UserService;
import com.scuoj.utils.JwtUtil;
import com.scuoj.utils.Md5Util;
import com.scuoj.utils.OjElemUtil;
import com.scuoj.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/api/user")
@Validated
//@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailService emailService;

    //由于匹配正则表达式，下面的if(username&&...)可以不需要

    /**
     * @param email    邮箱名
     * @param code     验证码
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @PostMapping("/register")
    public Result register(String email, String code, String username, String password) {

        if (username != null && username.length() >= 5 && username.length() <= 16
                && password != null && password.length() >= 5 && password.length() <= 16) {
            //查询用户
            User u = userService.findByUserName(username);
            if (u != null) return Result.error("用户名已被占用");
            //查询邮箱
            User u1 = userService.findByEmail(email);
            if (u1 != null) return Result.error("邮箱已被占用");
            // 验证邮箱
            if (!emailService.checkEmailCode(email, code)) return Result.error("验证码错误");

            //注册
            userService.register(email, username, password);
            return Result.success();

        } else {
            return Result.error("参数不合法");
        }
    }

    @PostMapping("/adminLogin")
    public Result adminLogin(String username, String password) {
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断用户是否存在
        if (loginUser == null) {
            return Result.error("用户名不存在");
        }
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }

        //判断密码是否正确
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            System.out.println(token);
            //把token存储到redis中
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    @PostMapping("/login")
    public Result login(String username, String password) {
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);
        //判断用户是否存在
        if (loginUser == null) {
            return Result.error("用户名不存在");
        }
//        System.out.println(username);
//        System.out.println(password);
        //判断密码是否正确
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            System.out.println(token);
            //把token存储到redis中
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);
            return Result.success(token);
        }

        return Result.error("密码错误");
    }

    @PostMapping("/emailLogin")
    public Result emailLogin(String email, String code) {
        if(userService.findByEmail(email) == null){
            return Result.error("邮箱错误");
        }
        if(emailService.checkEmailCode(email, code)){
            User loginUser = userService.findByEmail(email);

            if(loginUser == null){
                return Result.error("没有该用户");
            }
            if(!loginUser.getAuth().equals("user")){
                return Result.error("权限错误");
            }
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);

            User u = userService.findByEmail(email);
            u.setToken(token);
            return Result.success(u);
        }
        return Result.error("验证码错误");
    }

    //管理员邮箱登录
    @PostMapping("/adminEmailLogin")
    public Result adminEmailLogin(String email, String code) {
        if(emailService.checkEmailCode(email, code)){
            User loginUser = userService.findByEmail(email);

            if(loginUser == null){
                return Result.error("没有该用户");
            }
            if(loginUser.getAuth().equals("user")){
                return Result.error("权限错误");
            }
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", loginUser.getId());
            claims.put("username", loginUser.getUsername());
            String token = JwtUtil.genToken(claims);
            //把token存储到redis中
            ValueOperations<String,String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,token,1, TimeUnit.HOURS);

            User u = userService.findByEmail(email);
            u.setToken(token);
            return Result.success(u);
        }
        return Result.error("邮箱错误或验证码错误");
    }

    //查询当前登录用户信息
    @GetMapping("/userInfo")
    public Result<User> UserInfo(/*@RequestHeader(name = "Authorization") String token*/) {
        //根据用户名查询用户
//        Map<String,Object> map = JwtUtil.parseToken(token);
//        String username = (String) map.get("username");

        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    //更新用户信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user) {
//        System.out.println(user);
        //获取当前登录用户
        System.out.println(user);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");

        User loginUser = userService.findById(loginId);

        User u1 = userService.findByUserName(user.getUsername());
        if (u1 != null && u1.getUsername().equals(loginUser.getUsername())) {
            return Result.error("用户名已被占用");
        }

        User u2 = userService.findByEmail(user.getEmail());
        if (u2 != null && u2.getEmail().equals(loginUser.getEmail())) {
            return Result.error("邮箱重复");
        }
        userService.update(user);
        return Result.success();
    }

    //更新用户头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl) {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    //更新用户密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params, @RequestHeader("Authorization") String token) {
        //1.校验参数
//        String email = params.get("emali");
//        String code = params.get("code");
        String oldPwd = params.get("oldPwd");
        String newPwd = params.get("newPwd");
        String rePwd = params.get("rePwd");

//        //2.校验验证码
//        if (!emailService.checkEmailCode(email, code)) {
//            return Result.error("验证码错误");
//        }

//        System.out.println(oldPwd);
        if (!StringUtils.hasLength(oldPwd)) {
            return Result.error("原密码不能为空");
        }
        if (!StringUtils.hasLength(newPwd)) {
            return Result.error("新密码不能为空");
        }
        if (!StringUtils.hasLength(rePwd)) {
            return Result.error("确认密码不能为空");
        }

        //原密码是否正确
        //调用userService根据用户名拿到原密码，并于oldPwd比对
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if (!loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd))) {
            return Result.error("原密码填写不正确");
        }

        //newPwd和rePwd是否一样
        if (!rePwd.equals(newPwd)) {
            return Result.error("两次填写的新密码不相同");
        }

        //2.调用service完成密码更新
        userService.updatePwd(newPwd);
        //删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    //忘记密码
    @PostMapping("/forgetPwd")
    public Result forgetPwd(@RequestParam String email, @RequestParam String code, @RequestParam String newPwd) {
        //1.校验参数
        if (!StringUtils.hasLength(email)) {
            return Result.error("邮箱不能为空");
        }
        if (!StringUtils.hasLength(code)) {
            return Result.error("验证码不能为空");
        }
        if (!StringUtils.hasLength(newPwd)) {
            return Result.error("新密码不能为空");
        }

        if(userService.findByEmail(email) == null){
            return Result.error("邮箱不存在");
        }

        //2.校验验证码
        if (!emailService.checkEmailCode(email, code)) {
            return Result.error("验证码错误");
        }

        //3.调用service完成密码更新
        userService.updatePwdByEmail(email, newPwd);
        return Result.success();
    }

    //更新用户权限
    @PatchMapping("/updateAuth")
    public Result updateAuth(@RequestBody Map<String, String> params) {
        //查看当前登陆的用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        if (!loginUser.getAuth().equals("superAdmin")) {
            return Result.error("权限不足");
        }

        //查看需要修改权限的用户
        String username = params.get("username");
        User user = userService.findByUserName(username);
        if (user == null) {
            return Result.error("该用户名不存在");
        }
        if (user.getAuth().equals("Admin")) {
            return Result.error("该用户已经是管理员");
        }
        if (user.getAuth().equals("superAdmin")) {
            return Result.error("该用户已经是超级管理员");
        }
        userService.updateAuth(username);
        return Result.success();
    }

    //根据用户名查询用户信息
    @GetMapping("/findByUserName")
    public Result findByUserName(@RequestParam("username") String username) {
        User user = userService.findByUserName(username);
        if (user == null) {
            return Result.error("该用户名不存在");
        }
        return Result.success(user);
    }

    //根据用户名查询用户信息
    @PostMapping("/findByUserName1")
    public Result findByUserName1(@RequestParam("username") String username) {
        User user = userService.findByUserName(username);
        if (user == null) {
            return Result.error("该用户名不存在");
        }
        return Result.success(user);
    }

    //添加用户
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
//        System.out.println(token);
        System.out.println(user);
        //查看当前登陆的用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        //判断权限
        if (OjElemUtil.map.get(loginUser.getAuth()) >= OjElemUtil.map.get(user.getAuth())) {
            return Result.error("权限不足");
        }
        //判断用户名是否存在
        if (userService.findByUserName(user.getUsername()) != null) {
            return Result.error("该用户名已经存在");
        }
        //判断邮箱是否存在
        if(userService.findByEmail(user.getEmail()) != null) {
            return Result.error("该邮箱已存在");
        }

        userService.add(user);
        return Result.success();
    }

    //获取用户列表
    @GetMapping("/list")
    public Result list() {
//        System.out.println(token);
        //查看当前登陆的用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        if(loginUser.getAuth().equals("user")){
            return Result.error("权限不足");
        }
        List<User> userList = userService.list();
        List<User> resList = new ArrayList<>();
        for (User user : userList) {
            if(OjElemUtil.map.get(user.getAuth()) > OjElemUtil.map.get(loginUser.getAuth())){
                resList.add(user);
            }
        }
        return Result.success(resList);
    }

    //通过用户Id删除用户
    @DeleteMapping("/deleteById")
    public Result deleteById(@RequestParam Integer id) {
        //获取当前登录用户
//        System.out.println(id);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        //获取待删除用户
        User deleteUser = userService.findById(id);

        //判断权限
        if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(deleteUser.getAuth())){
            return Result.success("权限不足");
        }

        userService.deleteById(id);
        return Result.success("删除成功");
    }

    //通过用户Id列表删除用户列表
    @DeleteMapping("/deleteListById")
    public Result deleteListById(@RequestBody List<Integer> idList) {
        //获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);
        for(Integer id : idList){
            //获取待删除用户
            User deleteUser = userService.findById(id);

            //判断权限
            if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(deleteUser.getAuth())){
                return Result.success("权限不足");
            }

            userService.deleteById(id);
        }
        return Result.success("删除成功");
    }

    //通过用户名删除用户
    @DeleteMapping("/deleteByUsername")
    public Result deleteByUsername(@RequestParam String username) {
        //获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        //获取待删除用户
        User deleteUser = userService.findByUserName(username);

        //判断权限
        if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(deleteUser.getAuth())){
            return Result.success("权限不足");
        }

        userService.deleteByUsername(username);
        return Result.success("删除成功");
    }

    //通过用户名删除用户列表
    @DeleteMapping("/deleteListByUsername")
    public Result deleteListByUsername(@RequestBody List<String> usernameList) {
        //获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        for (String username : usernameList) {
            //获取待删除用户
            User deleteUser = userService.findByUserName(username);

            //判断权限
            if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(deleteUser.getAuth())){
                return Result.success("权限不足");
            }

            userService.deleteByUsername(username);
        }
        return Result.success("删除成功");
    }

    //更新用户信息
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        //获取当前登录用户
        System.out.println(user);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        //判断权限
        if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(user.getAuth())){
            return Result.error("权限不足");
        }

        if(loginUser.getAuth().equals(user.getAuth())){
            return Result.error("权限不足");
        }

        User u = userService.findById(user.getId());

        if(!u.getUsername().equals(user.getUsername())){
            User uu = userService.findByUserName(user.getUsername());
            if(uu!=null){
                return Result.error("用户名已被占用");
            }
        }

        if(!u.getEmail().equals(user.getEmail())){
            User uu = userService.findByEmail(user.getEmail());
            if(uu!=null){
                return Result.error("邮箱已被占用");
            }
        }

        userService.updateUser(user);
        return Result.success("更新用户成功");
    }

    //更新自己信息
    @PostMapping("/updateMyUser")
    public Result updateMyUser(@RequestBody User user) {
        //获取当前登录用户
        System.out.println(user);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        user.setId(loginUserId);

        if(!loginUser.getUsername().equals(user.getUsername())){
            User u = userService.findByUserName(user.getUsername());
            if(u!=null){
                return Result.error("用户名已存在");
            }
        }

        userService.updateMyInfo(user);
        return Result.success("更新用户成功");
    }

    //更新用户密码
    @PostMapping("/updateUserPwd")
    public Result updateUserPwd(@RequestBody User user) {
        //获取当前登录用户
        System.out.println(user);
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        //判断权限
        if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(user.getAuth())){
            return Result.success("权限不足");
        }

        userService.updateUserPwd(user);
        return Result.success("更新用户成功");
    }

    //获取权限为user的用户列表
    @GetMapping("/findByAuth")
    public Result findByAuth(
            @RequestParam(required = false) String auth
    ) {
        if(auth==null) auth = "user";
//        System.out.println(auth);
        //获取当前登录用户
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginUserId = (Integer) map.get("id");
        User loginUser = userService.findById(loginUserId);

        //判断权限
        if(OjElemUtil.map.get(loginUser.getAuth())>=OjElemUtil.map.get(auth)){
            return Result.success("权限不足");
        }
        List<User> userList = userService.findByAuth(auth);
        return Result.success(userList);
    }

    //判断当前登录人是否是用户
    @PostMapping("/isUserLogin")
    public Result isUserLogin(String username,String token){
        User user = userService.findByUserName(username);
        if(user==null){
            return Result.error("用户名不存在");
        }
        Map<String,Object> claims = JwtUtil.parseToken(token);
        if(claims.isEmpty()){
            return Result.error("token已经过期");
        }
        if(user.getAuth().equals("user")){
            return Result.success("该成员是用户");
        }
        else {
            return Result.error("该成员是管理员");
        }
    }

    //判断当前登录人是否是管理员
    @PostMapping("/isAdminLogin")
    public Result isAdminLogin(String username,String token){
        User user = userService.findByUserName(username);
        if(user==null){
            return Result.error("用户名不存在");
        }
        Map<String,Object> claims = JwtUtil.parseToken(token);
        if(claims.isEmpty()){
            return Result.error("token已经过期");
        }
        if(user.getAuth().equals("user")){
            return Result.error("该成员是用户");
        }
        else {
            return Result.success("该成员是管理员");
        }
    }
}
