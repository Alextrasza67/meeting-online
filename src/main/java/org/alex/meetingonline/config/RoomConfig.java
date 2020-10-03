package org.alex.meetingonline.config;

import org.alex.meetingonline.model.Room;
import org.alex.meetingonline.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 简单的room与用户配置
 */
public class RoomConfig {

    private static List<Room> roomList = new ArrayList<>();
    private final static Room EMPTY_ROOM = new Room().holder(new User()).initUserList();

    static {
        roomList.add(new Room().roomId("1").holder(new User("lily","123")).initUserList()
                .addUser(new User("zhang","123"))
                .addUser(new User("li","111"))
        );
        roomList.add(new Room().roomId("2").holder(new User("lucy","123")).initUserList()
                .addUser(new User("wang","321"))
                .addUser(new User("lu","222"))
        );
    }

    public static Room getRoom(String roomId){
        return roomList.stream().filter(item->item.getRoomId().equals(roomId)).findFirst().get();
    }
    public static boolean checkHolder(Room room, String userName, String password){
        return room.getHolder()!=null && room.getHolder().getName().equals(userName)&&room.getHolder().getPassword().equals(password);
    }
    public static boolean checkUser(Room room, String userName, String password){
        Optional<User> user = room.getUserList().stream().filter(item -> item.getName().equals(userName)&&item.getPassword().equals(password)).findFirst();
        return user.isPresent();
    }
}
