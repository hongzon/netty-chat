package com.nettychat.server.web.netty.bean;

import lombok.Data;

/**
 * Websocket 传输的内容实体
 */
@Data
public class Content {

    /**
     * 类型
     */
    private Integer action;

    /**
     * 聊天消息
     */
    private ChatMsg chatMsg;

    /**
     * 扩展字段
     */
    private String extend;
}
