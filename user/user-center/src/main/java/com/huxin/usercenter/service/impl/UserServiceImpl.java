package com.huxin.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huxin.usercenter.model.domain.User;
import com.huxin.usercenter.service.UserService;
import com.huxin.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.huxin.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author Avera
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-03-16 15:56:36
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    //盐值
    private static final String SALT = "huxintt";

    @Resource
    private UserMapper userMapper;

    /**
     * 用户注册
     * @param userAccount  用户账号
     * @param userPassword  用户密码
     * @param checkPassword  校验密码
     * @return 返回用户id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {


        //校验用户密码输入是否正确
        //to do  返回值应该是做一个类来统一管理返回值，方便前端进行接收
        if(StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return -1;
        }
        //校验账号长度
        if(userAccount.length() < 4){
            return -1;
        }
        //校验密码长度
        if (userPassword.length() < 8 && checkPassword.length() < 8){
            return -1;
        }
        //账号不能重复,需要从数据库中去查询 这里的这个方法没有用过，涉及到知识盲区了
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //这里是直接操作数据库底层
        queryWrapper.eq("userAccount",userAccount);
        //当前类里边会有这个方法嘛？
        long count = this.count(queryWrapper);
        if (count > 0){
            return -1;
        }
        //账户不能包含特殊字符，（可以自己去找对应的正则表达式，为什么要找正则表达式？）会灵活点匹配表达式
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            return -1;
        }
        //密码和校验密码是否相等，这里别用 == 号来比喻
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        //2，加密  这里的话该怎么使用时间戳来做盐给密码进行加密啊，这里的话是加入了一个简单的字符串来加密。
        //采用spring提供的一个加密工具类，MD5是一个单向加密数据的方法，这里我感觉是将其转换为了一个hex16进制，接收的参数是字节数组，采用String类
        //一个方法，将其转换为字节数组，
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
        //3，插入数据，这里有两种方法，
        //- 调用mapper类来进行操作数据库，
        //- 使用本类继承的ServiceImpl类的封装方法来实现
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        //这里需要判断一下数据的一个为空否，因为方法定义返回的是long类型，而字段属性是定义的Long类型（封装类），
        //所以当传进来的是一个null值的时候，拆箱会失败，
        if (!saveResult){
            return -1;
        }
        //返回用户id
        return user.getId();
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return 返回用户信息
     */
    @Override
        public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //校验用户密码输入是否正确
        //to do  返回值应该是做一个类来统一管理返回值，方便前端进行接收
        if(StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        //校验账号长度
        if(userAccount.length() < 4){
            return null;
        }
        //校验密码长度
        if (userPassword.length() < 8){
            return null;
        }

        //2，加密  这里的话该怎么使用时间戳来做盐给密码进行加密啊，这里的话是加入了一个简单的字符串来加密。
        //采用spring提供的一个加密工具类，MD5是一个单向加密数据的方法，这里我感觉是将其转换为了一个hex16进制，接收的参数是字节数组，采用String类
        //一个方法，将其转换为字节数组，
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));

        //账号不能重复,需要从数据库中去查询 这里的这个方法没有用过，涉及到知识盲区了
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //这里是直接操作数据库底层
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            return null;
        }
        //3，用户信息脱敏，将需要返回前端的值设定一下，
        User safetyUser = getSafetyUser(user);

        //4，记录用户的登录态,登录成功之后，将用户信息装进一个session中，以此来记录用户登录信息
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        return safetyUser;
    }

    /**
     *
     * 用户脱敏信息
     * @param originUser
     * @return 返回用户安全信息
     */
    @Override
    public User getSafetyUser(User originUser){
        //校验用户是否为空，如果为空，直接获取用户信息的话会抛异常
        if (originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUserName(originUser.getUserName());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }
}




