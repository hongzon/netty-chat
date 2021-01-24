package com.nettychat.server.pojo.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Data
public class RequestVO {
    /**
     * 请求者信息
     */
    private String sendUserId;
    private String sendUsername;
    private String sendFaceImage;
    private String sendNickname;
    /**
     * 请求信息
     */
    private String id;
    private Date requestDateTime;
}