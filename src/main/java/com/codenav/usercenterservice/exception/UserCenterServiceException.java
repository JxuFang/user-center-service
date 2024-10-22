package com.codenav.usercenterservice.exception;

import com.codenav.usercenterservice.common.ErrorCode;
import lombok.Getter;

@Getter
public class UserCenterServiceException extends RuntimeException {

    private final int code;

    private final String description;

    public UserCenterServiceException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public UserCenterServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getMessage();
    }

}