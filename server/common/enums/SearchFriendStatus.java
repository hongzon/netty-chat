package com.nettychat.server.common.enums;

import java.util.NoSuchElementException;


public enum SearchFriendStatus {

    /**
     * 搜索成功
     */
    SUCCESS(0, "OK"),

    /**
     * 搜索失败，无此用户
     */
    USER_NOT_EXIST(1, "此用户不存在..."),

    /**
     * 搜索成功，用户自己
     */
    NOT_YOURSELF(2, "不能添加自己..."),

    /**
     * 搜索成功，已添加该好友
     */
    ALREADY_FRIENDS(3, "该用户已经是你的好友...");

    public final int status;
    public final String msg;

    SearchFriendStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static SearchFriendStatus of(int status) {
        for (SearchFriendStatus value : SearchFriendStatus.values()) {
            if (value.status == status) {
                return value;
            }
        }
        throw new NoSuchElementException("没有该编码对应的状态：status = " + status);
    }
}
