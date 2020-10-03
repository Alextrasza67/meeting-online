package org.alex.meetingonline.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    private String roomId;
    private User holder;
    private List<User> userList;

    public Room roomId(String roomId){
        this.roomId = roomId;
        return this;
    }
    public Room holder(User holder){
        this.holder = holder;
        return this;
    }
    public Room initUserList(){
        userList = new ArrayList<>();
        return this;
    }
    public Room addUser(User user){
        this.userList.add(user);
        return this;
    }
}
