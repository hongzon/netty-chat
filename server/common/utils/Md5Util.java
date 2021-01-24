package com.nettychat.server.common.utils;

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

    /**
     * 对字符串进行 MD5 加密
     *
     * @param value 原文
     * @return Base64 编码后的 MD5 加密密文
     */
    public static String encode(String value) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return Base64.encodeBase64String(md5.digest(value.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
