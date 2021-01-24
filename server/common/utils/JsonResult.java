package com.nettychat.server.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 响应数据结构
 */
@Data
@AllArgsConstructor
public class JsonResult {
    /**
     * 响应业务请求情况
     */
    private Boolean success;

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 反馈消息
     */
    private String msg;

    /**
     * 反馈数据
     */
    private Object data;

    public static JsonResult data(Object data) {
        return new JsonResult(true, 200, "OK", data);
    }

    public static JsonResult msg(String msg) {
        return new JsonResult(false, 500, msg, null);
    }

    public static JsonResult success(Object data) {
        return data(data);
    }

    public static JsonResult success() {
        return success(null);
    }

    public static JsonResult error(String msg) {
        return msg(msg);
    }

    public static JsonResult error() {
        return error(null);
    }
}
