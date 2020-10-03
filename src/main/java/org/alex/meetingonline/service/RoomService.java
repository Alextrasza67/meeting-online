package org.alex.meetingonline.service;


import org.alex.meetingonline.model.RTCPeerConnection;
import org.alex.meetingonline.model.Room;
import org.alex.meetingonline.model.User;

public interface RoomService {

    /**
     * 主持人创建房间
     * @param holder
     * @return
     */
    Room createRoom(User holder);

    /**
     * 主持人添加用户
     * @param roomId
     * @param user
     * @return
     */
    User createUser(String roomId, User user);

    /**
     * 用户进入房间
     * @param user
     * @return
     */
    Room enterRoom(User user);

    /**
     * 用户退出房间
     * 如果是主持人退出则房间解散
     * // 用户退出 通知主持人/用户
     * @param user
     * @return
     */
    Room exitRoom(User user);

    /**
     * 通过id获取room
     * @param roomId
     * @return
     */
    Room getRoomById(String roomId);

    /**
     * 判断是否为room主持人
     * @param roomId
     * @param holder
     * @return
     */
    User checkIfRoomHolder(String roomId, User holder);

    /**
     * 判断是否为room用户
     * @param roomId
     * @param user
     * @return
     */
    User checkIfRoomUser(String roomId, User user);

    /**
     * 用户创建链接
     * @param roomId
     * @param user
     * @return
     */
    RTCPeerConnection createConnByUser(String roomId, User user);

    /**
     * 通过用户获取链接
     * @param roomId
     * @param user
     * @return
     */
    RTCPeerConnection getConnByUser(String roomId, User user);

    /**
     * 用户关闭链接
     * @param roomId
     * @param user
     */
    void closeConnByUser(String roomId, User user);




}
