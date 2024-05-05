package com.huxin.usercenter.service;


import com.huxin.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
   public void TestAddUser(){
        User user = new User();
        user.setUserName("huxin");
        user.setUserAccount("123456");
        user.setAvatarUrl("https://web-huxin.oss-cn-chengdu.aliyuncs.com/tt.jpg");
        user.setGender(1);
        user.setUserPassword("12345");
        user.setPhone("123445");
        user.setEmail("124515");
        user.setUserStatus(1);

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);

   }

}