package com.codenav.usercenterservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codenav.usercenterservice.common.ErrorCode;
import com.codenav.usercenterservice.exception.UserCenterServiceException;
import com.codenav.usercenterservice.mapper.UserMapper;
import com.codenav.usercenterservice.model.domain.User;
import com.codenav.usercenterservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.regex.Pattern;

import static com.codenav.usercenterservice.constant.UserConstant.LOGIN_STATE;


/**
 * 用户服务实现类
 *
 * @author jxfang
 * @date 2024/10/04
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值
     */
    private static final String SALT = "code-nav";

    private static final Pattern USER_ACCOUNT_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户和密码不能为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户长度不合法");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "密码长度不合法");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "二次密码不相同");
        }
        if (isInValidUserAccount(userAccount)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户不合法");
        }
        long count = this.count(Wrappers.lambdaQuery(User.class).eq(User::getUserAccount, userAccount));
        if (count > 0) {
            throw new UserCenterServiceException(ErrorCode.ALREADY_EXIST, "账户已经存在");
        }
        // 2. 加密
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptedPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new UserCenterServiceException(ErrorCode.SAVE_FAILED, "注册失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest httpServletRequest) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户和密码不能为空");
        }
        if (userAccount.length() < 4 || userAccount.length() > 16) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户长度不合法");
        }
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "密码长度不合法");
        }

        if (isInValidUserAccount(userAccount)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "用户账号不合法");
        }
        // 2. 加密
        String encryptedPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUserAccount, userAccount)
                .eq(User::getUserPassword, encryptedPassword);
        User user = this.getOne(queryWrapper);
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new UserCenterServiceException(ErrorCode.NOT_EXIST, "密码错误");
        }
        // 3. 脱敏
        User safelyUser = getSafelyUser(user);
        // 4. 记录用户的登录状态
        httpServletRequest.getSession().setAttribute(LOGIN_STATE, safelyUser);

        return safelyUser;
    }


    @Override
    public User getSafelyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safelyUser = new User();
        safelyUser.setId(originUser.getId());
        safelyUser.setUsername(originUser.getUsername());
        safelyUser.setUserAccount(originUser.getUserAccount());
        safelyUser.setAvatarUrl(originUser.getAvatarUrl());
        safelyUser.setGender(originUser.getGender());
        safelyUser.setPhone(originUser.getPhone());
        safelyUser.setEmail(originUser.getEmail());
        safelyUser.setUserStatus(originUser.getUserStatus());
        safelyUser.setUserRole(originUser.getUserRole());
        safelyUser.setCreateTime(new Date());
        return safelyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_STATE);
        return 1;
    }


    private static boolean isInValidUserAccount(String userAccount) {
        return !USER_ACCOUNT_PATTERN.matcher(userAccount).matches();
    }

}




