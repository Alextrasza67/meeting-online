package org.alex.meetingonline.config;

import org.alex.meetingonline.model.Room;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class RoomConfigTest {

    @Test
    public void test(){
        Room room1 = RoomConfig.getRoom("1");
        Room room2 = RoomConfig.getRoom("2");
        assertTrue(RoomConfig.checkHolder(room1,"lily","123"));
        assertTrue(RoomConfig.checkUser(room1,"zhang","123"));
        assertTrue(RoomConfig.checkUser(room2,"lu","222"));
        assertFalse(RoomConfig.checkUser(room2,"zhang","123"));
    }
}