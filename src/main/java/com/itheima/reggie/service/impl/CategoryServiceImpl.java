package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/18:04
 * @Description:
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>implements CategoryService {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Transactional

    //对分类信息的删除操作
    @Override
    public void removeWith(Long ids) {

        //根据分类id将对应的菜品和套餐信息查询出来
       //创建条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId,ids);
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,ids);

        int count = dishService.count(dishLambdaQueryWrapper);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);

        if (count>0||count1>0){

            //他们之间有至少有一样是和分类id关联 不能进行删除 抛出异常
            throw new CustomException("该分类和菜品或套餐有关联 不能删除");
        }else {
            //删除分类
            categoryService.removeById(ids);
        }
    }
}
