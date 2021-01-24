package com.nettychat.server.web.service;

import com.nettychat.server.common.enums.SearchFriendStatus;
import com.nettychat.server.pojo.Msg;
import com.nettychat.server.pojo.User;
import com.nettychat.server.pojo.vo.FriendVO;
import com.nettychat.server.pojo.vo.RequestVO;
import com.nettychat.server.web.netty.bean.ChatMsg;

import java.util.List;


public interface UserService {

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户进行登录
     *
     * @param username
     * @param password
     * @return
     */
    User queryUserForLogin(String username, String password);

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    User saveUser(User user);

    /**
     * 更新用户
     *
     * @param user
     * @return
     */
    User updateUserInfo(User user);

    /**
     * 搜索朋友的前置条件
     *
     * @param userId
     * @param friendUsername
     * @return
     */
    SearchFriendStatus preconditionSearchFriend(String userId, String friendUsername);

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */
    User queryUserByUsername(String username);

    /**
     * 添加好友请求
     *
     * @param userId
     * @param friendUsername
     */
    void sendFriendRequest(String userId, String friendUsername);

    /**
     * 查询好友请求信息
     *
     * @param acceptUserId
     * @return
     */
    List<RequestVO> queryFriendRequest(String acceptUserId);

    /**
     * 删除好友请求
     *
     * @param requestId
     */
    void deleteFriendRequest(String requestId);

    /**
     * 忽略好友请求
     *
     * @param requestId
     */
    void ignoreFriendRequest(String requestId);

    /**
     * 通过好友请求
     *
     * @param requestId
     * @param acceptUserId
     * @param sendUserId
     */
    void passFriendRequest(String requestId, String acceptUserId, String sendUserId);

    /**
     * 查询好友列表
     *
     * @param userId
     * @return
     */
    List<FriendVO> queryFriend(String userId);

    /**
     * 保存聊天消息
     *
     * @param chatMsg
     * @return 消息 ID
     */
    Msg saveMsg(ChatMsg chatMsg);

    /**
     * 更新消息的签收状态
     *
     * @param msgIdList
     */
    void updateMsgSignFlag(List<String> msgIdList);

    /**
     * 查询为接收消息
     *
     * @param acceptUserId
     * @return
     */
    List<ChatMsg> queryUnsignedMsg(String acceptUserId);
}
