package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:50
 * @Description:
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    //联表添加操作，和菜品一样的逻辑思路
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //先添加套餐信息，再将对应的菜品信息添加进去
        setmealService.save(setmealDto);

        //获取该套餐id
        Long setmealId = setmealDto.getId();

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealId);
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    //根据id将数据回显出来
    @Override
    public SetmealDto getByIdWithDish(Long id) {
        Setmeal setmeal = setmealService.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);

        //获取对应的菜品id
        Long dishId = setmeal.getId();

        //根据该id获取对应的联表信息

        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SetmealDish::getSetmealId,dishId);

        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    //修改套餐信息
    @Override
    public void updateWithDish(SetmealDto setmealDto) {

        //修改套餐信息
        setmealService.updateById(setmealDto);

        Long dishId = setmealDto.getId();
        //删除菜品信息

        //创建条件构造器
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,dishId);

        setmealDishService.remove(queryWrapper);
        //添加修改后的菜品信息
        List<SetmealDish> list = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : list) {

            setmealDish.setSetmealId(dishId);

            //保存
            setmealDishService.save(setmealDish);
        }
    }
}
