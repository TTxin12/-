package com.huxin.usercenter.contant;

/**
 * 常量接口类
 * @author huxin
 */
public interface UserContant {


    /**
     * 用户角色常量
     * 0-普通用户  默认权限
     * 1-管理员
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";
}
