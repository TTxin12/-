package com.huxin.usercenter;

import com.huxin.usercenter.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class UserCenterApplicationTests {

    @Resource
    private UserService userService;

    @Test
    void contextLoads() {
    }

    @Test
    void userRegisterTest(){
//        String userAccount = "123";
//        String userPassword = "";
//        String checkPassword = "1234567";
//        //调用方法来测试，查看返回值是否正常
//          long result = userService.userRegister(userAccount,userPassword,checkPassword);
//        //断言来测试返回值,已知这个方法的返回值是多少，
//        Assertions.assertEquals(-1,result);
//        //测试账号长度
//        userAccount = "tt";
//        result = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//        //验证密码长度
//        userAccount = "huxin";
//        userPassword = "123456";
//        result = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//        //校验特殊字符
//        userAccount = "hu xin";
//        userPassword = "12345678";
//        result = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//        //校验密码和注册密码不一致
//        checkPassword = "123456789";
//        result = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
//        //加入正常账号
//        userAccount = "huxintt";
//        checkPassword = "12345678";
//        result = userService.userRegister(userAccount,userPassword,checkPassword);
//        Assertions.assertEquals(-1,result);
        //
        String userAccount = "huxintt";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertTrue(result > 0);

    }
}
