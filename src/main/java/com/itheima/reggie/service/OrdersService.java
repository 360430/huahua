package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.Orders;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/27/10:55
 * @Description:
 */
public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
