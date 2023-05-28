package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/18:06
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page>page(int page,int pageSize){
        //创建分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Category::getSort);

        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 删除分类信息
     */
    @DeleteMapping
    public R<String>remove(Long ids){

        //这里需要进行联表，所以需要开启事务性注解驱动，并判断删除的分类是否和其他菜品或套餐有关联，如果有关联就抛出异常没有就直接删除逻辑
        categoryService.removeWith(ids);

        return  R.success("菜品分类删除成功");
    }

    /**
     * 新增菜品或套餐的类型
     */
    @PostMapping
    public R<String>save(@RequestBody Category category){

        categoryService.save(category);
        return R.success("添加分类成功");
    }

    /**
     * 修改菜品或套餐分类
     */
    @PutMapping
    public R<String>update(@RequestBody Category category){

        categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    /**
     * 查询菜品或套餐分类
     */
    @GetMapping("/list")
    public R<List<Category>>getById(Integer type){

        log.info("类型是:{}",type);
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=type,Category::getType,type);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
