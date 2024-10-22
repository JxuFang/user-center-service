package com.codenav.usercenterservice.common;

/**
 * 返回结果工具类
 * @author jxfang
 * @date 2024/10/13
 */
public class ResultUtils {

    private ResultUtils() {}

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "success", "");
    }

    public static BaseResponse<String> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static BaseResponse<String> error(int code, String message, String description) {
        return new BaseResponse<>(code, null, message, description);
    }
}