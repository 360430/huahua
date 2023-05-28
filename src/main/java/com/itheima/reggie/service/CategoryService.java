package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Category;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/18:03
 * @Description:
 */
public interface CategoryService extends IService<Category> {
    @Transactional
    void removeWith(Long ids);
}
