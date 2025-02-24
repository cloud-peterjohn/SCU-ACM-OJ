package com.scuoj.interceptors;

import com.scuoj.utils.JwtUtil;
import com.scuoj.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    //拦截器
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception{
        //令牌验证
        String token = request.getHeader("Authorization");
//        System.out.println(token);
        //验证token
        try{
//            //从redis中获取相同的token
//            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
//            String redisToken = operations.get(token);
//            if(redisToken == null){
//                //token已经失效了
//                throw new RuntimeException();
//            }
            Map<String,Object> claims = JwtUtil.parseToken(token);

            //将业务数据存储到ThreadLocal中
            ThreadLocalUtil.set(claims);
            //通过
            return true;
        }catch (Exception e){
//            System.out.println('o');
            //http响应状态码401
            response.setStatus(401);
            //不通过
            return false;
        }
    }

    //不再使用的时候记得清楚，防止内存泄露
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清空ThreadLocal中的数据
        ThreadLocalUtil.remove();
    }
}
