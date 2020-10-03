package org.alex.meetingonline.controller;


import lombok.extern.slf4j.Slf4j;
import org.alex.meetingonline.config.RoomConfig;
import org.alex.meetingonline.model.Authentication;
import org.alex.meetingonline.model.Room;
import org.alex.meetingonline.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.util.List;
import java.util.Optional;


@Controller
@EnableWebSocket
@Slf4j
public class OnlineMeetingWithAuthSocket {


    private final SimpMessagingTemplate messagingTemplate;
    /*
     * 实例化Controller的时候，注入SimpMessagingTemplate
     */
    @Autowired
    public OnlineMeetingWithAuthSocket(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/meetingRoomServer/{roomId}")
    public void meetingRoomServer(@DestinationVariable String roomId, String message, StompHeaderAccessor headerAccessor) throws Exception {
        Authentication curUser = (Authentication) headerAccessor.getUser();
        log.info("recive message : {}",message);
        Room room = RoomConfig.getRoom(roomId);
        List<User> userList = room.getUserList();
        Optional<User> uo = userList.stream().filter(user->!user.getName().equals(curUser.getName())).findFirst();
        if(uo.isPresent()){
            log.info("notify user : {}", uo.get().getName());
            messagingTemplate.convertAndSendToUser(uo.get().getName(), "/meetingRoom/"+roomId, message);
        }
        return ;
    }


}
