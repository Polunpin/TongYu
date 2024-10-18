package com.TongYu.config;

import lombok.Data;

import java.util.HashMap;

@Data
public final class ApiPageResponse {

    private Integer code;
    private String errorMsg;
    private int startIndex;
    private int pageSize;
    private int total;
    private Object data;

    private ApiPageResponse(Integer code, String errorMsg, int startIndex, int pageSize, int total, Object data) {
        this.code = code;
        this.errorMsg = errorMsg;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.total = total;
        this.data = data;
    }

    public static ApiPageResponse ok() {
        return new ApiPageResponse(0, "", 0, 10, 0, new HashMap<>());
    }

    public static ApiPageResponse ok(int startIndex, int pageSize, int total, Object data) {
        return new ApiPageResponse(0, "", startIndex, pageSize, total, data);
    }

    public static ApiPageResponse error(Integer code, int startIndex, int pageSize, int total, String errorMsg) {
        return new ApiPageResponse(code, errorMsg, startIndex, pageSize, total, new HashMap<>());
    }
}
