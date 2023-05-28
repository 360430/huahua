package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:46
 * @Description:
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>implements DishService {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    //根据id查询菜品和口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {

        //查询菜品信息
        Dish dish = dishService.getById(id);

        Long dishId = dish.getId();

        //创建条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);

        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);

        DishDto dishDto = new DishDto();

        //对象的对拷
        BeanUtils.copyProperties(dish,dishDto);

        dishDto.setFlavors(flavorList);

        return dishDto;
    }

    //修改菜品和删除再保存口味信息
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //更新菜品信息
        dishService.updateById(dishDto);

        //删除口味信息
        Long dishId = dishDto.getId();
        dishFlavorService.removeById(dishId);

        //保存新的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {

            //将菜品id关联上口味信息
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);
    }
}
