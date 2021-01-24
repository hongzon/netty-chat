package com.nettychat.server.common.enums;

import java.util.NoSuchElementException;

/**
 * Websocket 传输的内容实体对应的动作枚举
 *
 */
public enum ContentAction {

    CONNECT(1, "连接消息"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "签收消息"),
    KEEP_ALIVE(4, "心跳消息"),
    PULL_FRIEND(5, "拉取好友消息"),
    ;

    public final int code;
    public final String msg;

    ContentAction(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ContentAction of(int code) {
        for (ContentAction value : ContentAction.values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new NoSuchElementException("没有该动作类型：code = " + code);
    }
}
