package com.nettychat.server.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
public class Friend {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "friend_user_id")
    private String friendUserId;
}