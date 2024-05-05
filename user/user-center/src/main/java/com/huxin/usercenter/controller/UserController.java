package com.huxin.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.huxin.usercenter.model.domain.User;
import com.huxin.usercenter.model.request.UserLoginRequest;
import com.huxin.usercenter.model.request.UserRegisterRequest;
import com.huxin.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.huxin.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.huxin.usercenter.contant.UserContant.USER_LOGIN_STATE;

@RestController  //一种编程风格  restful风格  返回的数据格式默认为json格式
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册请求
     */
    @PostMapping("/register")
    //@RequestBody  该注解表明将前端传回的数据与UserRegisterRequest 对象进行匹配
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        //在进行一次校验，如果校验不成功就直接返回，不用换进入业务逻辑层（service）
        //获取请求体中的参数时，需要规范一下格式
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        //判断参数是否为空
        boolean anyBlank = StringUtils.isAnyBlank(userAccount, userPassword, checkPassword);
        if (anyBlank){
            return null;
        }
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    /**
     * 用户登录请求
     */
    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        //在进行一次校验，如果校验不成功就直接返回，不用换进入业务逻辑层（service）
        //获取请求体中的参数时，需要规范一下格式
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        //判断参数是否为空
        boolean anyBlank = StringUtils.isAnyBlank(userAccount,userPassword);
        if (anyBlank){
            return null;
        }
        return userService.userLogin(userAccount,userPassword,request);
    }

    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    @GetMapping("/current")
    public User Current(HttpServletRequest request){
        //从request中得到session中的用户信息,这里返回的是object类，
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        //强转为user对象
        User currentUser = (User) userObj;
        //校验
        if (currentUser == null){
            return null;
        }
        long userId = currentUser.getId();
        //todo 校验用户是否合法，如果是一个空用户的话，获取id时会报错
        //这里的话数据会持久变化，所以直接从数据库中取数据，而不从session缓存中取，及时更新信息
        User user = userService.getById(userId);
        return userService.getSafetyUser(user);
    }


    /**
     *
     * 查找用户
     * @param username
     * @param request
     * @return
     */
    @GetMapping("/search")
    public List<User> searchUsers(String username,HttpServletRequest request){
         if (!isAdmin(request)){
             //不能返回空值，就返回一个list类型的空数组
             return new ArrayList<>();
         }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            //获取查询条件，进行查询，这个like的话，不给定后边的username参数，就会进行拼接模糊查询
            queryWrapper.like("userName",username);
        }
        //脱敏一下管理员查询的用户信息
        List<User> userList = userService.list(queryWrapper);
        //lambda表达式
        return userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    /**
     *
     * 删除用户
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/delete")
    public boolean deleteUsers(@RequestBody long id,HttpServletRequest request){
        if (!isAdmin(request)){
            //不能返回空值，就返回一个list类型的空数组
            return false;
        }
        if (id < 0){
            return false;
        }
        return userService.removeById(id);
    }

    /**
     *
     * 获取用户全限，从request请求里，然后判断是否是管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        //从request中得到session中的用户信息,这里返回的是object类，
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        //强转为user，需要判断是否为空，但是这里不用判断
        User user = (User) userObj;
        //需要判断用户信息是否是为空
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
}
