package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/23/16:33
 * @Description:
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 前端发送用户名的电话号码 并发送短信
     * @param
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String>sendMsg(@RequestBody Map<String,String>map, HttpServletRequest request){

        //获取用户手机号
        String phone = map.get("phone");

        //获取验证码
        Integer code = ValidateCodeUtils.generateValidateCode(4);
        log.info("验证码:{}",code);

        //将验证码保存在session中，之后再通过手机号来找的对应的验证码
        request.getSession().setAttribute(phone,code);

        //TODO 将session保存在redis缓存中并设置耗时时间
        redisTemplate.opsForValue().set(phone,code,5L, TimeUnit.MINUTES);

        return R.success("验证码发送成功");
    }

    /**
     * 根据手机号码进行登录
     */
    @PostMapping("/login")
    public R<User>login (@RequestBody Map<String,String>map,HttpServletRequest request){

        log.info("键值对:{}",map);

        //获取前端发送过来的手机号码和短信验证码
        String phone = map.get("phone");
        String code = map.get("code");

        //获取存在在session中的验证码
        String codeInSession = request.getSession().getAttribute(phone).toString();

        //TODO 获取存在redis中的缓存数据
        String codeInSession2 = redisTemplate.opsForValue().get(phone).toString();
        //将前端发送的code和存储在session中的验证码进行比对
        if (!code.equals(codeInSession2)){
            //验证码校验错误
            return R.error("验证码错误,请重新尝试");
        }
        //验证码校验成功 TODO 将reids缓存的验证码删除掉
        redisTemplate.delete(phone);


        //根据该手机号来查询数据库是否存在该号码,存在就直接带着该号码返回,不存在则在数据库中先添加该号码并返回该用户信息
            //创建条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);

        User user = userService.getOne(queryWrapper);

        if (user==null){

            //数据库中不存在该号码
            user=new User();
            user.setPhone(phone);

            //将该用户信息保存在数据库当中
            userService.save(user);

            //将该用户id保存在session当中,在拦截器中校验
            request.getSession().setAttribute("user",user.getId());
            return R.success(user);
        }else {
            //将该用户id保存在session当中,在拦截器中校验
            request.getSession().setAttribute("user",user.getId());

            //数据库存在该号码
            return R.success(user);
        }

    }
}
