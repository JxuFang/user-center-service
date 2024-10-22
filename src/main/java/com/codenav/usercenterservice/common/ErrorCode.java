package com.codenav.usercenterservice.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    PARAM_ERROR(40001, "请求参数错误"),
    ALREADY_EXIST(40002, "已存在"),
    NOT_EXIST(40003, "不存在"),
    SAVE_FAILED(40004, "数据保存失败"),

    NO_AUTHORIZATION(40100, "无权限"),
    NO_LOGIN(40200, "未登录"),


    ;

    private final int code;

    private final String message;


    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}