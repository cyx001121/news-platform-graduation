package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.UserService;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody User user){
        userService.add(user);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable  Integer id){
        userService.deleteById(id);
        return Result.success();
    }


    @DeleteMapping("/delete/Batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        userService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody User user){
        userService.updateById(user);
        return Result.success();
    }
    @GetMapping("/select/{id}")
    public Result selectById(@PathVariable Integer id){
        User user =userService.selectById(id);
        return Result.success(user);
    }

    @GetMapping("/selectAll")
    public Result selectAll(User user){
        List<User> userList=userService.selectAll(user);
        return Result.success(userList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(User user,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue =" 10") Integer pageSize){
        PageInfo<User> page = userService.selectPage(user,pageNum,pageSize);
        return Result.success(page);
    }
//
//    @PostMapping("/register")
//    public Result register(@RequestBody Account account){
//        if (StrUtil.isBlank(account.getUsername())
//                ||StrUtil.isBlank(account.getPassword())||StrUtil.isBlank(account.getRole())){//检测注册的账号是否包含空格或者为空
//            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
//        }
//       return Result.success();
//    }

}
