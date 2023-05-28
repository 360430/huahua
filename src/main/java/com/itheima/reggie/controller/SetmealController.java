package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:50
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加套餐信息
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>save(@RequestBody SetmealDto setmealDto){
        log.info(setmealDto.toString());

        setmealService.saveWithDish(setmealDto);
        return R.success("套餐信息保存成功");
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){

        //创建分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //创建条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(null!=name,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo,queryWrapper);

        //查询之后将对象进行对拷操作
        BeanUtils.copyProperties(page,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto>list=new ArrayList<>();
        for (Setmeal record : records) {

            SetmealDto setmealDto = new SetmealDto();

            //对象进行对拷
            BeanUtils.copyProperties(record,setmealDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (null!=category){
                String categoryName = category.getName();

                setmealDto.setCategoryName(categoryName);

                list.add(setmealDto);
            }
            setmealDtoPage.setRecords(list);
        }

        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐信息
     */
    @DeleteMapping
    public R<String>delete(Long[]ids){

        for (Long id : ids) {
            setmealService.removeById(id);
        }
        return R.success("删除成功");
    }

    /**
     * 修改套餐信息
     */
    @PostMapping("/status/{status}")
    public R<String>update(@PathVariable int status,Long[] ids){

        for (Long id : ids) {
            Setmeal setmeal = setmealService.getById(id);

            setmeal.setStatus(status);

            setmealService.updateById(setmeal);
        }

        return R.success("修改套餐状态成功");
    }

    /**
     * 根据id查询套餐数据
     */
    @GetMapping("/{id}")
    public R<SetmealDto>getById(@PathVariable Long id){

        SetmealDto setDto = setmealService.getByIdWithDish(id);
        return R.success(setDto);
    }

    /**
     * 修改套餐信息
     */
    @PutMapping
    public R<String>update(@RequestBody SetmealDto setmealDto){

        log.info(setmealDto.toString());
        setmealService.updateWithDish(setmealDto);

        return R.success("套餐修改成功");
    }

    /**
     * 查询套餐列表
     */
    @GetMapping("/list")
    public R<List<SetmealDto>>list(Integer status,Long categoryId){

        //在查询数据库之前建立redis缓存
        String key="setmeal_"+categoryId+"_1";
      List<SetmealDto>setmealDtoList= (List<SetmealDto>) redisTemplate.opsForValue().get(key);

      if (null!=setmealDtoList){
          return R.success(setmealDtoList);
      }

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,categoryId).eq(Setmeal::getStatus,status);

        //查询套餐列表
        List<Setmeal> list = setmealService.list(queryWrapper);

        //将数据进行对拷
        List<SetmealDto>setmealDto=new ArrayList<>();
        BeanUtils.copyProperties(list,setmealDto);

        for (Setmeal setmeal : list) {

            //数据对拷
            SetmealDto setmealDto1 = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto1);

            Long setmealId = setmeal.getId();

            LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(SetmealDish::getSetmealId,setmealId);

            List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper1);

            setmealDto1.setSetmealDishes(setmealDishes);
            setmealDto.add(setmealDto1);
        }
        //保存一份数据给redis缓存进来
        redisTemplate.opsForValue().set(key,setmealDto);

        return R.success(setmealDto);
    }
}
