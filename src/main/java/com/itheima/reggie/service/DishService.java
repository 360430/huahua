package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:45
 * @Description:
 */
public interface DishService extends IService<Dish> {
    @Transactional
    DishDto getByIdWithFlavor(Long id);

    @Transactional
    void updateWithFlavor(DishDto dishDto);
}
