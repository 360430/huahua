package com.itheima.reggie.common;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/17:21
 * @Description:
 */
//工具类
public class BaseContext {
    private static ThreadLocal<Long>threadLocal=new ThreadLocal<>();

    public static void setCurrent(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrent() {
        return threadLocal.get();
    }
}
