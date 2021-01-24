package com.nettychat.server.common.enums;

import java.util.NoSuchElementException;

/**
 * 消息签收状态
 */
public enum SignFlag {

    UNSIGNED(0, "未签收"),
    SIGNED(1, "已签收"),
    ;

    public final int state;
    public final String des;

    SignFlag(int state, String des) {
        this.state = state;
        this.des = des;
    }

    public static SignFlag of(int state) {
        for (SignFlag value : SignFlag.values()) {
            if (value.state == state) {
                return value;
            }
        }
        throw new NoSuchElementException("没有该签收状态：state = " + state);
    }
}
