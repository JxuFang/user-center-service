package com.codenav.usercenterservice.service;

import com.codenav.usercenterservice.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 * @author jxfang
 * @date 2024/10/04
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("jxfang");
        user.setUserAccount("123");
        user.setAvatarUrl("11111");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "123";
        String userPassword = "";
        String checkPassword = "";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userPassword = "123456";
        checkPassword = "123456";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userAccount = "1234";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userPassword = "12345678";
        checkPassword = "123456789";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        userPassword = "12345678";
        checkPassword = "12345678";
        userAccount = "jxfang@";
        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        Assertions.assertEquals(-1, userService.userRegister(userAccount, userPassword, checkPassword));

        Assertions.assertTrue(userService.userRegister(userAccount, userPassword, checkPassword) > 0);
    }
}