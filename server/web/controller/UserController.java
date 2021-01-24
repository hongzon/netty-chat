package com.nettychat.server.web.controller;

import com.nettychat.server.common.enums.RequestOperationType;
import com.nettychat.server.common.enums.SearchFriendStatus;
import com.nettychat.server.common.utils.FileUtil;
import com.nettychat.server.common.utils.JsonResult;
import com.nettychat.server.pojo.User;
import com.nettychat.server.pojo.bo.UserBO;
import com.nettychat.server.pojo.vo.UserVO;
import com.nettychat.server.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${storage.path}")
    private String storagePath;

    @Autowired
    private UserService userService;

    /**
     * 注册或登录
     *
     * @param user
     * @return
     */
    @PostMapping("/register-or-login")
    public JsonResult registerOrLogin(@RequestBody User user) {
        if (StringUtils.isAnyBlank(user.getUsername(), user.getPassword())) {
            return JsonResult.error("用户名或密码不能为空！");
        }
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        User userResult;
        if (usernameIsExist) {
            // 登录
            userResult = userService.queryUserForLogin(user.getUsername(), user.getPassword());
            if (userResult == null) {
                return JsonResult.error("用户名或密码错误！");
            }
        } else {
            // 注册
            userResult = userService.saveUser(user);
        }
        return JsonResult.success(toUserVO(userResult));
    }

    /**
     * 头像上传
     *
     * @param userBO
     * @return
     */
    @PostMapping("/upload-face")
    public JsonResult uploadFaceBase64(@RequestBody UserBO userBO) {
        String base64Data = userBO.getFaceData();
        String userFaceImgName = "avatar/" + userBO.getUserId() + ".png";
        String userFacePath = storagePath + userFaceImgName;
        FileUtil.base64ToFile(userFacePath, base64Data);
        log.debug("userFacePath: " + userFacePath);

        User user = new User();
        user.setId(userBO.getUserId());
        user.setFaceImage(userFaceImgName);
        User userResult = userService.updateUserInfo(user);
        return JsonResult.success(toUserVO(userResult));
    }

    /**
     * 昵称修改
     *
     * @param userBO
     * @return
     */
    @PostMapping("/set-nickname")
    public JsonResult setNickname(@RequestBody UserBO userBO) {
        User user = new User();
        user.setId(userBO.getUserId());
        user.setNickname(userBO.getNickname());
        User userResult = userService.updateUserInfo(user);
        return JsonResult.success(toUserVO(userResult));
    }

    /**
     * 好友搜索
     *
     * @param userId
     * @param friendUsername
     * @return
     */
    @GetMapping("/search")
    public JsonResult searchUser(String userId, String friendUsername) {
        if (StringUtils.isAnyBlank(userId, friendUsername)) {
            return JsonResult.error("参数错误！");
        }
        SearchFriendStatus searchFriendStatus = userService.preconditionSearchFriend(userId, friendUsername);
        if (searchFriendStatus == SearchFriendStatus.SUCCESS) {
            User friendUser = userService.queryUserByUsername(friendUsername);
            return JsonResult.success(toUserVO(friendUser));
        } else {
            return JsonResult.error(searchFriendStatus.msg);
        }
    }

    /**
     * 添加好友请求
     *
     * @param userId
     * @param friendUsername
     * @return
     */
    @GetMapping("/add-friend-request")
    public JsonResult addFriendRequest(String userId, String friendUsername) {
        if (StringUtils.isAnyBlank(userId, friendUsername)) {
            return JsonResult.error("参数错误！");
        }
        SearchFriendStatus searchFriendStatus = userService.preconditionSearchFriend(userId, friendUsername);
        if (searchFriendStatus == SearchFriendStatus.SUCCESS) {
            userService.sendFriendRequest(userId, friendUsername);
            return JsonResult.success();
        } else {
            return JsonResult.error(searchFriendStatus.msg);
        }
    }

    /**
     * 查询好友请求
     *
     * @param userId
     * @return
     */
    @GetMapping("/query-friend-request")
    public JsonResult queryFriendRequest(String userId) {
        if (StringUtils.isBlank(userId)) {
            return JsonResult.error("参数错误！");
        }
        return JsonResult.success(userService.queryFriendRequest(userId));
    }

    /**
     * 操作好友请求
     *
     * @param operationType
     * @param requestId
     * @param acceptUserId
     * @param sendUserId
     * @return
     */
    @GetMapping("/operate-friend-request")
    public JsonResult operateFriendRequest(Integer operationType, String requestId, String acceptUserId, String sendUserId) {
        if (operationType == null || StringUtils.isAnyBlank(requestId, acceptUserId, sendUserId)) {
            return JsonResult.error("参数错误！");
        }
        RequestOperationType requestOperationType = RequestOperationType.of(operationType);
        if (requestOperationType == RequestOperationType.IGNORE) {
            userService.ignoreFriendRequest(requestId);
        } else {
            userService.passFriendRequest(requestId, acceptUserId, sendUserId);
        }
        return JsonResult.success(userService.queryFriend(acceptUserId));
    }

    /**
     * 查询好友列表
     *
     * @param userId
     * @return
     */
    @GetMapping("/friend-list")
    public JsonResult friendList(String userId) {
        if (StringUtils.isBlank(userId)) {
            return JsonResult.error("参数错误！");
        }
        return JsonResult.success(userService.queryFriend(userId));
    }

    private UserVO toUserVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
