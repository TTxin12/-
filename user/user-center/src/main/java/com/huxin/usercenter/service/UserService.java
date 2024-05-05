package com.huxin.usercenter.service;

import com.huxin.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author Avera
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-03-16 15:56:36
*/
public interface UserService extends IService<User> {

    /**
     *用户注册服务
     *
     * @param userAccount  用户账号
     * @param userPassword  用户密码
     * @param checkPassword  校验密码
     * @return
     */
    long userRegister(String userAccount,String userPassword,String checkPassword);

    /**
     * 用户登录服务
     *
     * @param userAccount
     * @param userPassword
     * @return  返回脱敏之后的id
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User originUser);
}
