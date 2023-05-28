package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/27/10:57
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单操作 需要考虑购物车是否是空，地址信息是否有误，下单成功之后需要将购物车清空
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String>submit(@RequestBody Orders orders){

        log.info(orders.toString());

        ordersService.submit(orders);
        return R.success("用户下单成功");
    }

    /**
     * 订单分页查询
     */
    @GetMapping("/page")
    public R<Page>page(Integer page,Integer pageSize,String number){

        log.info("页码数:{},每页展示记录数:{}",page,pageSize);

        //创建分页构造器
        Page<Orders> pageInfo = new Page<>(page, pageSize);

        //创建条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=number,Orders::getNumber,number);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrent());
        queryWrapper.orderByDesc(Orders::getCheckoutTime);

        ordersService.page(pageInfo);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     */
    @PutMapping
    public R<String>update(@RequestBody Orders orders){

        //修改订单状态
        ordersService.updateById(orders);
        return R.success("修改订单成功");
    }
}
