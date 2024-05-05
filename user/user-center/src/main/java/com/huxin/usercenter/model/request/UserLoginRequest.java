package com.huxin.usercenter.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体（前端返回数据）
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 999097853712708197L;

    //设置唯一序列id，防止后边序列化的时候出错
    //private static final long serialVersionUID = 2260506268994811327L;


    /**
     * 用户账号
     */
    private String userAccount;
    /**
     * 用户密码
     */
    private String userPassword;
}
