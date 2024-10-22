package com.codenav.usercenterservice.exception;

import com.codenav.usercenterservice.common.BaseResponse;
import com.codenav.usercenterservice.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionAdvice {

    @ExceptionHandler(UserCenterServiceException.class)
    public BaseResponse<String> userCenterServiceException(UserCenterServiceException e) {
        log.error(e.getMessage(), e);
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }
}