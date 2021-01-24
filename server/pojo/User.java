package com.nettychat.server.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
public class User {
    @Id
    private String id;

    private String username;

    private String password;

    @Column(name = "face_image")
    private String faceImage;

    private String nickname;

    private String qrcode;

    private String cid;
}