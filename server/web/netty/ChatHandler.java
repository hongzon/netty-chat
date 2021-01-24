package com.nettychat.server.web.netty;

import com.nettychat.server.common.enums.ContentAction;
import com.nettychat.server.common.utils.JsonUtil;
import com.nettychat.server.pojo.Msg;
import com.nettychat.server.web.netty.bean.ChatMsg;
import com.nettychat.server.web.netty.bean.Content;
import com.nettychat.server.web.service.UserService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


@Slf4j
@Sharable
@Component
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 用于记录和管理所有客户端的 channel
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 用于记录和管理 userId 与 channel 关系
     */
    private static ConcurrentHashMap<String, Channel> userIdChannelMap = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msgFrame) throws Exception {
        Channel channel = ctx.channel();
        // 获取客户端传输过来的消息
        Content content = JsonUtil.toObj(msgFrame.text(), Content.class);
        // log.debug("[{}] 发送消息：{}", channel.remoteAddress(), content);
        if (content != null) {
            // 根据消息类型，进行不同的业务
            int action = content.getAction();
            switch (ContentAction.of(action)) {
                case CONNECT: {
                    // 连接：建立连接时，将 channel 与 userId 进行进行关联
                    ChatMsg chatMsg = content.getChatMsg();
                    String userId = chatMsg.getSendUserId();
                    log.debug("CONNECT 消息: userId={}, channel={}", userId, channel.id().asShortText());
                    userIdChannelMap.put(userId, channel);
                    // 获取未签收信息并发送
                    content.setAction(ContentAction.CHAT.code);
                    userService.queryUnsignedMsg(userId)
                            .forEach(msg -> {
                                content.setChatMsg(msg);
                                channel.writeAndFlush(new TextWebSocketFrame(JsonUtil.toJson(content)));
                            });
                    break;
                }
                case CHAT: {
                    // 聊天：把聊天记录保存到数据，并标记消息的签收状态为未签收
                    ChatMsg chatMsg = content.getChatMsg();
                    log.debug("CHAT 消息: chatMsg={}", chatMsg);
                    Msg msg = userService.saveMsg(chatMsg);
                    chatMsg.setMsgId(msg.getId());
                    chatMsg.setSendTime(msg.getCreateTime());
                    // 发送消息
                    sendTo(chatMsg.getAcceptUserId(), content);
                    break;
                }
                case SIGNED: {
                    // 签收：针对具体的消息进行签收，标记消息的签收状态为已签收
                    String extend = content.getExtend();
                    // 扩展字段在 SIGNED 类型的消息中，代表需要进行签收的消息 ID，多个时使用逗号分隔
                    String[] msgIdArr = extend.split(",");
                    List<String> msgIdList = Arrays.stream(msgIdArr)
                            .filter(msgId -> !"".equals(msgId))
                            .collect(Collectors.toList());
                    log.debug("SIGNED 消息: msgIdArr={}, msgIdList={}", Arrays.toString(msgIdArr), msgIdList);
                    userService.updateMsgSignFlag(msgIdList);
                    break;
                }
                // 心跳：心跳消息，维持连接
                case KEEP_ALIVE: {
                    //log.debug("KEEP_ALIVE 消息: channel={}", channel.id().asShortText());
                    break;
                }
                default:
                    log.debug("未知消息类型");
            }
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 当客户端连接服务端之后，获取客户端的 channel，放到 ChannelGroup 中进行管理
        channelGroup.add(ctx.channel());
        log.debug("客户端连接: channel={}", ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发 handlerRemoved 方法时，从 ChannelGroup 移除对应的客户端的 channel
        channelGroup.remove(ctx.channel());
        log.debug("客户端断开: channel={}", ctx.channel().id().asShortText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Websocket 发生异常", cause);
        // 关闭 channel 并移除
        ctx.channel().close();
        channelGroup.remove(ctx.channel());
    }

    /**
     * 发送消息
     *
     * @param acceptUserId
     * @param content
     */
    public void sendTo(String acceptUserId, Content content) {
        Optional.ofNullable(userIdChannelMap.get(acceptUserId))
                .ifPresent(acceptChannel -> {
                    if (channelGroup.contains(acceptChannel)) {
                        // 用户在线
                        acceptChannel.writeAndFlush(new TextWebSocketFrame(JsonUtil.toJson(content)));
                    } else {
                        // 用户离线，移除关联
                        userIdChannelMap.remove(acceptUserId);
                    }
                });
    }
}
