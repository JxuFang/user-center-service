package com.codenav.usercenterservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codenav.usercenterservice.model.domain.User;

import javax.servlet.http.HttpServletRequest;


/**
 * 用户服务
 * @author jxfang
 * @date 2024/10/04
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);


    /**
     * 用户登录
     *
     * @param userAccount        用户账户
     * @param userPassword       用户密码
     * @param httpServletRequest {@link HttpServletRequest}
     * @return {@link User } 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest);

    /**
     * 脱敏用户信息
     *
     * @param originUser 原用户信息
     * @return {@link User }
     */
    User getSafelyUser(User originUser);


    /**
     * 用户登出
     * @param httpServletRequest
     * @return int
     */
    int userLogout(HttpServletRequest httpServletRequest);
}
