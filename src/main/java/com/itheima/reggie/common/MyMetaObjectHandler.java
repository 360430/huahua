package com.itheima.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/17:17
 * @Description:
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.fillStrategy(metaObject,"createTime", LocalDateTime.now());
        this.fillStrategy(metaObject,"createUser",BaseContext.getCurrent());

        this.fillStrategy(metaObject,"updateTime",LocalDateTime.now());
        this.fillStrategy(metaObject,"updateUser",BaseContext.getCurrent());

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.fillStrategy(metaObject,"updateTime",LocalDateTime.now());
        this.fillStrategy(metaObject,"updateUser", BaseContext.getCurrent());

    }
}
