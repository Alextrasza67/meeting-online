package org.alex.meetingonline.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class User {

    private String name;
    private String password;

    public User(String name, String password){
        this.name = name;
        this.password = password;
    }


}
