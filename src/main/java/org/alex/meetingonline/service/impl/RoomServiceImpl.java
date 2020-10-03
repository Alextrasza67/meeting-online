package org.alex.meetingonline.service.impl;

import org.alex.meetingonline.model.RTCPeerConnection;
import org.alex.meetingonline.model.Room;
import org.alex.meetingonline.model.User;
import org.alex.meetingonline.service.RoomService;

public class RoomServiceImpl implements RoomService {
    @Override
    public Room createRoom(User holder) {
        return null;
    }

    @Override
    public User createUser(String roomId, User user) {
        return null;
    }

    @Override
    public Room enterRoom(User user) {
        return null;
    }

    @Override
    public Room exitRoom(User user) {
        return null;
    }

    @Override
    public Room getRoomById(String roomId) {
        return null;
    }

    @Override
    public User checkIfRoomHolder(String roomId, User holder) {
        return null;
    }

    @Override
    public User checkIfRoomUser(String roomId, User user) {
        return null;
    }

    @Override
    public RTCPeerConnection createConnByUser(String roomId, User user) {
        return null;
    }

    @Override
    public RTCPeerConnection getConnByUser(String roomId, User user) {
        return null;
    }

    @Override
    public void closeConnByUser(String roomId, User user) {

    }
}
