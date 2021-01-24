package com.nettychat.server.common.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.IOException;


public class FileUtil {
    public static boolean base64ToFile(String filePath, String base64Data) {
        String data;
        if (base64Data == null || "".equals(base64Data)) {
            return false;
        } else {
            String[] d = base64Data.split("base64,");
            if (d.length == 2) {
                data = d[1];
            } else {
                return false;
            }
        }
        try {
            byte[] bs = Base64Utils.decodeFromString(data);
            FileUtils.writeByteArrayToFile(new File(filePath), bs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
