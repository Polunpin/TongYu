package com.TongYu.config;

import lombok.Data;

import java.util.HashMap;

@Data
public final class ApiResponse {

    private Integer code;
    private Integer total;
    private String errorMsg;
    private Object data;

    private ApiResponse(Integer code, Integer total, String errorMsg, Object data) {
        this.code = code;
        this.total = total;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public static ApiResponse ok(Object data) {
        return new ApiResponse(0, 0, "", data);
    }

    public static ApiResponse ok( Integer total,Object data) {
        return new ApiResponse(0, total, "", data);
    }

    public static ApiResponse error(String errorMsg) {
        return new ApiResponse(0, 0, errorMsg, new HashMap<>());
    }
}
