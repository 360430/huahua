package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:49
 * @Description:
 */
public interface SetmealService extends IService<Setmeal> {
    @Transactional
    void saveWithDish(SetmealDto setmealDto);

    @Transactional
    SetmealDto getByIdWithDish(Long id);

    @Transactional
    void updateWithDish(SetmealDto setmealDto);
}
