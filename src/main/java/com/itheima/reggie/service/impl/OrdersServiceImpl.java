package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.OrdersDto;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/27/10:55
 * @Description:
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders>implements OrdersService {

    @Autowired
    private OrdersService ordersService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private UserService userService;
    //用户下单
    @Override
    public void submit(Orders orders) {

        Long userId = BaseContext.getCurrent();
        User user = userService.getById(userId);

        //查询当前购物车数量
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrent());

        List<ShoppingCart> shoppingCarts = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        if (shoppingCarts==null){
            //购物车为空，不能去下单，抛出异常
            throw new CustomException("购物车为空,请重新添加购物车后下单");
        }
        //购物车不为空

            //获取地址信息
            Long addressBookId = orders.getAddressBookId();

            //创建条件构造器
            LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrent());
            queryWrapper.eq(AddressBook::getId,addressBookId);

            AddressBook addressBook = addressBookService.getOne(queryWrapper);

            if (addressBook==null){
                throw new CustomException("用户地址信息有误,请重新填写");
            }

        long orderId = IdWorker.getId();//订单号

        AtomicInteger amount = new AtomicInteger(0);

        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());


        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);
//        shoppingCartService.remove(wrapper);
        //都插入之后需要清空购物车 TODO 这个以前从来没有使用过啊 得看看老师是怎么弄得 这个只需要调用shoppingCartsservice.remove方法就行了 条件只要是userId就行
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }






    }

