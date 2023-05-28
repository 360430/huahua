package com.itheima.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/15:07
 * @Description:
 */
@Component
@Slf4j
public class LoginCheckFilter implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        log.info("拦截的路径:{}",request.getRequestURI());
        Long empId = (Long) request.getSession().getAttribute("employee");
        Long userId = (Long) request.getSession().getAttribute("user");

        //在拦截器中将该empId存放在统一自动填充的工具类中
        BaseContext.setCurrent(empId);
        BaseContext.setCurrent(userId);

        if (empId!=null){
            return true;
        }
        if (userId!=null){
            return true;
        }else {
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;

        }
/*
        //客户端
        if (empId==null||userId==null){
            //没有登录，重定向登录页面进行登录；
//            response.sendRedirect("/backend/page/login/login.html");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }
        else {
            return true;
        }

        //移动端
        if (userId==null){
            //没有登录，重定向登录页面进行登录；
//            response.sendRedirect("/backend/page/login/login.html");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }
        else {
            return true;
        }*/



    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
