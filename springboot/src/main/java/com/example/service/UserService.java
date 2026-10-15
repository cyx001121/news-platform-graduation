package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Constants;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;


import javax.annotation.Resource;
import java.util.List;

@Service
public class UserService {

@Resource
private UserMapper userMapper;

    public void add(User user){
        User dbUser=userMapper.selectByUsername(user.getUsername());
        if (dbUser!=null){
            throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);//检查是否存在相同用户名
        }
        if (ObjectUtil.isEmpty(user.getPassword())){
            user.setPassword(Constants.USER_DEFAULT_PASSWORD);//设置默认密码
        }
        if (ObjectUtil.isEmpty(user.getUsername())){
            user.setName(user.getUsername());//如未取名则设置用户名为名字
        }
        user.setRole(RoleEnum.User.name());
        userMapper.insert(user);
    }

    public void deleteById(Integer id){
        userMapper.deleteById(id);
    }
    //删除

    public void deleteBatch(List<Integer> ids){
        for (Integer id : ids){
            deleteById(id);
        }
    }
    //全部删除

    public void updateById(User user){
        userMapper.updateById(user);
    }
    //修改

    public User selectById(Integer id){
        return userMapper.selectById(id);
    }
    //查询用户信息

    public List<User> selectAll(User user){
        return userMapper.selectAll(user);
    }
    //查询所有用户

    public PageInfo<User> selectPage(User user,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<User> list = userMapper.selectAll(user);
        return PageInfo.of(list);
    }

    public Account login(Account account){
        Account dbUser = userMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbUser)){
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);//数据库内无信息
        }
        if (!dbUser.getPassword().equals(account.getPassword())){
            throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);//检查密码
        }
        String tokenData =dbUser.getId()+"-"+RoleEnum.User.name();
        String token = TokenUtils.createToken(tokenData,dbUser.getPassword());
        dbUser.setToken(token);
        return dbUser;
    }

    public void register(Account account){
        User user = new User();
        BeanUtils.copyProperties(account,user);
        this.add(user);
    }

    public void updatePassword(Account account) {
        User dbUser = userMapper.selectByUsername(account.getUsername());
//        System.out.println(dbUser+"更新前");
        if (ObjectUtil.isNull(dbUser)){
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if(!account.getPassword().equals(dbUser.getPassword())){
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbUser.setPassword(account.getNewPassword());
        this.updateById(dbUser);
//        System.out.println(dbUser+"更新后");
    }
}
