package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/22/8:46
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品信息
     */
    @PostMapping
    public R<String>save(@RequestBody DishDto dishDto){

        log.info(dishDto.toString());
        //先添加菜品信息
        dishService.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();

        //获取前面发送过来的口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //将口味信息保存在对应菜品id的口味表中
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
            dishFlavorService.save(flavor);
        }

        //TODO 添加之后缓存的数据和数据库对应的数据不一致，需要将redis缓存的数据删除掉
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("新增菜品和口味成功");
    }

    /**
     * 分页查询操作
     */
    @GetMapping("/page")
    public R<Page>page(int page,int pageSize,String name){

        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(null!=name,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //分页查询
        dishService.page(pageInfo,queryWrapper);

        //对拷
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto>list=new ArrayList<>();

        for (Dish record : records) {

            DishDto dishDto = new DishDto();

            //对拷
            BeanUtils.copyProperties(record,dishDto);

            //获取分类id
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category!=null){

                //说明分类存在,获取分类名称
                String categoryName = category.getName();

                dishDto.setCategoryName(categoryName);

                //将分类名称给保存到list集合中
                list.add(dishDto);
            }

            dishDtoPage.setRecords(list);
        }

        return R.success(dishDtoPage);
    }

    /**
     * 修改菜品信息之根据id查询菜品和口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto>getById(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品之保存菜品和口味信息
     */
   /* @PutMapping
    public R<String>update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);
        return R.success("菜品和口味信息修改成功");
    }*/

    //TODO redis更新缓存优化
    @PutMapping
    public R<String>update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        //TODO 修改之后将原先缓存的数据删除掉
        Long categoryId = dishDto.getCategoryId();
        String key="dish_"+categoryId+"_1";
        redisTemplate.delete(key);

        return R.success("菜品和口味信息修改成功");
    }
    /**
     * 修改菜品停售起售状态
     */
    @PostMapping("/status/{status}")
    public R<String>updateStatus(@PathVariable int status,Long[]ids){

        log.info("修改菜品停售和起售状态");

        for (Long id : ids) {

            Dish dish = dishService.getById(id);
            dish.setStatus(status);

            //修改
            dishService.updateById(dish);
        }
        return R.success("修改菜品状态成功");
    }

    /**
     * 删除菜品信息
     */
    @DeleteMapping
    public R<String>delete(Long[]ids){

        log.info(Arrays.toString(ids));

        for (Long id : ids) {
            //遍历数组进行删除操作
            dishService.removeById(id);
        }

        //TODO 和前面相同的redis删除逻辑
        String key="dish_*";
        redisTemplate.delete(key);

        return R.success("菜品删除成功");
    }

    /**
     * 根据id查询菜品信息
     */
    /*@GetMapping("/list")
    public R<List<Dish>>list(Long categoryId){

        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/

    /*@GetMapping("/list")
    public R<List<DishDto>>list(Long categoryId){

        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = dishService.list(queryWrapper);

        //对象对拷
        List<DishDto>dishDtoList=new ArrayList<>();
        BeanUtils.copyProperties(list,dishDtoList);

        for (Dish dish : list) {

            //对象对拷
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);


            Long dishId = dish.getId();

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(flavorList);
            dishDtoList.add(dishDto);
        }

        return R.success(dishDtoList);
    }*/

    //TODO 缓存查询菜品优化
    @GetMapping("/list")
    public R<List<DishDto>>list(Long categoryId){

        //创建缓存，在数据库查询之前
        String key="dish_"+categoryId+"_1";

        //获取菜品信息
       List<DishDto> dishDtoList1= (List<DishDto>) redisTemplate.opsForValue().get(key);

       if (null!=dishDtoList1){
           return R.success(dishDtoList1);
       }

        //创建条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,categoryId);

        List<Dish> list = dishService.list(queryWrapper);

        //对象对拷
        List<DishDto>dishDtoList=new ArrayList<>();
        BeanUtils.copyProperties(list,dishDtoList);

        for (Dish dish : list) {

            //对象对拷
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);


            Long dishId = dish.getId();

            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);

            List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper1);

            dishDto.setFlavors(flavorList);
            dishDtoList.add(dishDto);
        }

        //经历第一次数据库查询后将数据添加在redis缓存中
        redisTemplate.opsForValue().set(key,dishDtoList);

        return R.success(dishDtoList);
    }
}
