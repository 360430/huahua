package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/14:19
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee>login(@RequestBody Employee employee, HttpServletRequest request){

        log.info(employee.toString());

        //将密码进行md5加密处理
        String employeePassword = employee.getPassword();
        String password = DigestUtils.md5DigestAsHex(employeePassword.getBytes());

        String username = employee.getUsername();

        //判断用户名和密码是否同时相同

        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,username).eq(Employee::getPassword,password);

        Employee emp = employeeService.getOne(queryWrapper);

        if (emp==null){
            //不是同时相同则返回错误提示信息
            return R.error("用户名或密码错误");
        }

        else {
            //相同则返回正确信息并将userid存储在session中
            request.getSession().setAttribute("employee",emp.getId());
            return R.success(emp);
        }
    }

    @PostMapping("/logout")
    public R<String>logout(HttpServletRequest request){
        log.info("登出");

        //登出过程中将session总携带的数据删除掉
        request.removeAttribute("employee");

        return R.success("登出成功");
    }

    @GetMapping("/page")
    public R<Page>page(int page, int pageSize, String name){
        log.info("分页查询");
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=name,Employee::getName,name);

        //分页查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 添加员工
     */
    @PostMapping
    public R<String>save(@RequestBody Employee employee){

        //设置初始密码
        String userPassword="123456";
        String password = DigestUtils.md5DigestAsHex(userPassword.getBytes());

        //将密码保存在新员工信息中
        employee.setPassword(password);

        //直接将用户信息保存
        employeeService.save(employee);
        return R.success("员工信息保存成功");
    }

    /**
     * 修改员工状态
     */
    @PutMapping
    public R<String>update(@RequestBody Employee employee){

        employeeService.updateById(employee);
        return R.success("修改员工状态成功");
    }

    /**
     * 编辑员工的数据回显
     */
    @GetMapping("/{id}")
    public R<Employee>getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);

        return R.success(employee);
    }


}
