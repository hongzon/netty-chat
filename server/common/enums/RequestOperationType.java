package com.nettychat.server.common.enums;

import java.util.NoSuchElementException;


public enum RequestOperationType {

    /**
     * 忽略请求
     */
    IGNORE(0, "忽略"),

    /**
     * 通过1请求
     */
    PASS(1, "通过");

    public final int type;
    public final String msg;

    RequestOperationType(int type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public static RequestOperationType of(int type) {
        for (RequestOperationType value : RequestOperationType.values()) {
            if (value.type == type) {
                return value;
            }
        }
        throw new NoSuchElementException("没有该操作类型：state = " + type);
    }
}
