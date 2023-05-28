package com.itheima.reggie.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/17:38
 * @Description:
 */
@RestControllerAdvice
@Configuration
public class GlobalExceptionHandler {

    @ExceptionHandler
        public R<String>exceptionHandler(SQLIntegrityConstraintViolationException icve){
            String message = icve.getMessage();
            String[] split = message.split(" ");
            String userName = split[2];

          return   R.error(userName+"已存在 请重新添加新用户");
        }

    @ExceptionHandler
    public R<String>customException(CustomException customException){

        String message = customException.getMessage();
        return R.error(message);
    }
}
