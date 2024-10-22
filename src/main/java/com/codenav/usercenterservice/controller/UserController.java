package com.codenav.usercenterservice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codenav.usercenterservice.common.BaseResponse;
import com.codenav.usercenterservice.common.ErrorCode;
import com.codenav.usercenterservice.common.ResultUtils;
import com.codenav.usercenterservice.constant.RoleEnum;
import com.codenav.usercenterservice.exception.UserCenterServiceException;
import com.codenav.usercenterservice.model.domain.User;
import com.codenav.usercenterservice.model.request.UserLoginRequest;
import com.codenav.usercenterservice.model.request.UserRegisterRequest;
import com.codenav.usercenterservice.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.codenav.usercenterservice.constant.UserConstant.LOGIN_STATE;

/**
 * 用户接口
 *
 * @author jxfang
 * @date 2024/10/05
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(LOGIN_STATE);
        if (currentUser == null) {
            throw new UserCenterServiceException(ErrorCode.NO_LOGIN);
        }
        User user = userService.getById(currentUser.getId());
        User safelyUser = userService.getSafelyUser(user);
        return ResultUtils.success(safelyUser);
    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户和密码不能为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "账户和密码不能为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR);
        }
        return ResultUtils.success(userService.userLogout(request));
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(@RequestParam(required = false) String username, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            throw new UserCenterServiceException(ErrorCode.NO_AUTHORIZATION);
        }
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class);
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> result = userList.stream().map(userService::getSafelyUser).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (isNotAdmin(request)) {
            throw new UserCenterServiceException(ErrorCode.NO_AUTHORIZATION);
        }
        if (id <= 0) {
            throw new UserCenterServiceException(ErrorCode.PARAM_ERROR, "用户不存在");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 是否是 Admin 角色
     * @return boolean
     */
    private boolean isNotAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(LOGIN_STATE);
        return user == null || user.getUserRole() != RoleEnum.ADMIN.getValue();
    }
}