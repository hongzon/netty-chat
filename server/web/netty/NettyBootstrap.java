package com.nettychat.server.web.netty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


@Component
public class NettyBootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            webSocketServer.start();
        }
    }
}
