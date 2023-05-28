package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/23/18:27
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart>add(@RequestBody ShoppingCart shoppingCart){

        log.info("查询购物车数据");

        //TODO 相当于和userId绑定 新增的条件需要是对应的id 即查询管理员用户id 指定当前用户是哪一个购物车数据
        Long userId = BaseContext.getCurrent();

        shoppingCart.setUserId(userId);

        //获取菜品id 先来判断如果是null 说明是套餐 否则是菜品
        Long dishId = shoppingCart.getDishId();

        //然后保证是同一个userId 同一个dishId或setmealId来进行条件查询
        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId!=null){

            //说明添加菜品购物车
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else {

            //说明添加套餐购物车
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //条件查询
        ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);

        if (serviceOne!=null){

            //数据库存在该信息，直接在number基础上+1就行
            Integer number = serviceOne.getNumber();

            serviceOne.setNumber(number+1);

            //更新操作
            shoppingCartService.updateById(serviceOne);

            return R.success(serviceOne);
        }else {

            //因为前端并没有传递number给服务端 所以需要在这里定义一个number传给数据库
            shoppingCart.setNumber(1);
            //数据库不存在该信息，直接保存即可
            shoppingCartService.save(shoppingCart);

            return R.success(shoppingCart);
        }
    }

    /**
     * 展示购物车装载数
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>>list(){

        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 减少购物车
     */
    @PostMapping("/sub")
    public R<String>sub(@RequestBody ShoppingCart shoppingCart){

        log.info(shoppingCart.toString());

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();

        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());

        if (dishId!=null){
            //减少的是菜品购物车
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            shoppingCartService.remove(queryWrapper);
        }else {
            //减少的是套餐购物车
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
            shoppingCartService.remove(queryWrapper);
        }
        return R.success("减少购物车成功");
    }

    /**
     * 清空购物车车
     */
    @DeleteMapping("/clean")
    public R<String>clean(){

        //创建条件构造器
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

}
