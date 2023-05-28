package com.itheima.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/23/16:31
 * @Description:
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
