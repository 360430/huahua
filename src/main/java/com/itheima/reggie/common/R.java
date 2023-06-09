package com.itheima.reggie.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@ApiModel("返回结果类")
public class R<T> implements Serializable {

    @ApiModelProperty("响应状态码")
    private Integer code; //编码：1成功，0和其它数字为失败

    @ApiModelProperty("返回的信息成功/失败")
    private String msg; //错误信息

    @ApiModelProperty("返回的数据")
    private T data; //数据

    @ApiModelProperty("返回的动态数据")
    private Map map = new HashMap(); //动态数据

    public static <T> R<T> success(T object) {
        R<T> r = new R<T>();
        r.data = object;
        r.code = 1;
        return r;
    }

    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

}
