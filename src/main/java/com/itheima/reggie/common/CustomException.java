package com.itheima.reggie.common;

import org.springframework.stereotype.Component;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:56
 * @Description:
 */
@Component
public class CustomException extends RuntimeException{
    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }
}
