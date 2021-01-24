package com.nettychat.server.web.service.impl;

import com.nettychat.server.common.enums.ContentAction;
import com.nettychat.server.common.enums.SearchFriendStatus;
import com.nettychat.server.common.enums.SignFlag;
import com.nettychat.server.common.utils.Md5Util;
import com.nettychat.server.common.utils.QRCodeUtil;
import com.nettychat.server.mapper.*;
import com.nettychat.server.pojo.Friend;
import com.nettychat.server.pojo.Msg;
import com.nettychat.server.pojo.Request;
import com.nettychat.server.pojo.User;
import com.nettychat.server.pojo.vo.FriendVO;
import com.nettychat.server.pojo.vo.RequestVO;
import com.nettychat.server.web.service.UserService;
import com.nettychat.server.mapper.*;
import com.nettychat.server.web.netty.ChatHandler;
import com.nettychat.server.web.netty.bean.ChatMsg;
import com.nettychat.server.web.netty.bean.Content;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Value("${storage.path}")
    private String storagePath;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FriendMapper friendMapper;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private MsgMapper msgMapper;

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private ChatHandler chatHandler;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
        User record = new User();
        record.setUsername(username);
        User res = userMapper.selectOne(record);
        return res != null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserForLogin(String username, String password) {
        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", Md5Util.encode(password));
        return userMapper.selectOneByExample(userExample);
    }

    @Transactional
    @Override
    public User saveUser(User user) {
        String userId = Sid.nextShort();
        user.setId(userId);
        user.setPassword(Md5Util.encode(user.getPassword()));
        user.setNickname(user.getUsername());
        user.setFaceImage("avatar/default-avatar.png");
        // 生成二维码
        String qrCodeName = "qrcode/" + userId + ".png";
        QRCodeUtil.createQRCode(storagePath + qrCodeName, "sticky-chat-user:" + user.getUsername());
        user.setQrcode(qrCodeName);
        // 插入数据库
        userMapper.insert(user);
        return user;
    }

    @Transactional
    @Override
    public User updateUserInfo(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return queryUserById(user.getId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public SearchFriendStatus preconditionSearchFriend(String userId, String friendUsername) {
        User friendUserResult = queryUserByUsername(friendUsername);
        if (friendUserResult == null) {
            return SearchFriendStatus.USER_NOT_EXIST;
        }
        if (friendUserResult.getId().equals(userId)) {
            return SearchFriendStatus.NOT_YOURSELF;
        }
        Example myFriendExample = new Example(Friend.class);
        Criteria criteria = myFriendExample.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("friendUserId", friendUserResult.getId());
        Friend friendResult = friendMapper.selectOneByExample(myFriendExample);
        return Optional.ofNullable(friendResult)
                .map((item) -> SearchFriendStatus.ALREADY_FRIENDS)
                .orElse(SearchFriendStatus.SUCCESS);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public User queryUserByUsername(String username) {
        Example userExample = new Example(User.class);
        Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username", username);
        return userMapper.selectOneByExample(userExample);
    }

    @Transactional
    @Override
    public void sendFriendRequest(String userId, String friendUsername) {
        User friend = queryUserByUsername(friendUsername);
        Example requestExample = new Example(Request.class);
        Criteria criteria = requestExample.createCriteria();
        criteria.andEqualTo("sendUserId", userId);
        criteria.andEqualTo("acceptUserId", friend.getId());
        Request request = requestMapper.selectOneByExample(requestExample);
        if (request == null) {
            request = new Request();
            request.setId(Sid.nextShort());
            request.setSendUserId(userId);
            request.setAcceptUserId(friend.getId());
            request.setRequestDateTime(new Date());
            requestMapper.insert(request);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<RequestVO> queryFriendRequest(String acceptUserId) {
        return customMapper.queryFriendRequest(acceptUserId);
    }

    @Transactional
    @Override
    public void deleteFriendRequest(String requestId) {
        requestMapper.deleteByPrimaryKey(requestId);
    }

    @Transactional
    @Override
    public void ignoreFriendRequest(String requestId) {
        deleteFriendRequest(requestId);
    }

    @Transactional
    @Override
    public void passFriendRequest(String requestId, String acceptUserId, String sendUserId) {
        // 保存好友
        saveFriend(acceptUserId, sendUserId);
        // 逆向保存好友
        saveFriend(sendUserId, acceptUserId);
        // 删除好友请求
        deleteFriendRequest(requestId);
        // 使用 websocket 主动推送消息到请求发起者，通知其更新通讯录
        Content content = new Content();
        content.setAction(ContentAction.PULL_FRIEND.code);
        chatHandler.sendTo(sendUserId, content);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendVO> queryFriend(String userId) {
        return customMapper.queryFriend(userId);
    }

    @Transactional
    @Override
    public Msg saveMsg(ChatMsg chatMsg) {
        Msg msg = new Msg();
        msg.setId(Sid.nextShort());
        msg.setSendUserId(chatMsg.getSendUserId());
        msg.setAcceptUserId(chatMsg.getAcceptUserId());
        msg.setMsg(chatMsg.getMsg());
        msg.setSignFlag(SignFlag.UNSIGNED.state);
        msg.setCreateTime(new Date());
        msgMapper.insert(msg);
        return msg;
    }

    @Override
    public void updateMsgSignFlag(List<String> msgIdList) {
        if (!CollectionUtils.isEmpty(msgIdList)) {
            customMapper.batchUpdateMsgSignFlag(msgIdList);
        }
    }

    @Override
    public List<ChatMsg> queryUnsignedMsg(String acceptUserId) {
        return customMapper.queryUnsignedMsg(acceptUserId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public User queryUserById(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Transactional
    public void saveFriend(String userId, String friendUserId) {
        Friend friend = new Friend();
        friend.setId(Sid.nextShort());
        friend.setUserId(userId);
        friend.setFriendUserId(friendUserId);
        friendMapper.insert(friend);
    }
}
