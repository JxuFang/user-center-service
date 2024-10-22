package com.codenav.usercenterservice.constant;

import lombok.Getter;

@Getter
public enum RoleEnum {

    ORDINARY(0),

    ADMIN(1)
    ;
    private final int value;
    RoleEnum(int value) {
        this.value = value;
    }

}
