package com.nettychat.server.pojo.vo;

import lombok.Data;

@Data
public class FriendVO {
    private String id;
    private String friendUserId;
    private String friendUsername;
    private String friendFaceImage;
    private String friendNickname;
}