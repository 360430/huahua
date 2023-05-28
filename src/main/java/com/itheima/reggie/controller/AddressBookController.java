package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/26/8:51
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 添加收货地址
     */
    @PostMapping
    public R<AddressBook>save(@RequestBody AddressBook addressBook){

        //将该用户id保存到收货地址中
        Long userId = BaseContext.getCurrent();
        addressBook.setUserId(userId);

        //添加
        addressBookService.save(addressBook);

        return R.success(addressBook);
    }

    /**
     * 地址信息展示
     * @return
     */
    @GetMapping("/list")
    public R<List<AddressBook>>list(){

        //创建条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrent());

        List<AddressBook> list = addressBookService.list(queryWrapper);

        return R.success(list);
    }

    @PutMapping("/default")
    public R<AddressBook>update(@RequestBody AddressBook addressBook){

        log.info(addressBook.toString());
        Long userId = BaseContext.getCurrent();

        //创建条件构造器
        LambdaUpdateWrapper<AddressBook> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AddressBook::getUserId,userId);
        updateWrapper.set(AddressBook::getIsDefault,0);

        //先将所有的地址都设置为非默认地址
        addressBookService.update(addressBook,updateWrapper);

        //然后将该地址设置为默认地址
        addressBook.setIsDefault(1);

        Long id = addressBook.getId();

        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getId,id);

        //进行更新操作
        addressBookService.update(addressBook,queryWrapper);

        return R.success(addressBook);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("/default")
    public R<AddressBook>getDefault(){

        //创建条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrent());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        return R.success(addressBook);
    }
}
