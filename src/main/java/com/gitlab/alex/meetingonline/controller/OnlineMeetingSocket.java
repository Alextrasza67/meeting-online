package com.gitlab.alex.meetingonline.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Vector;


@Component
@EnableWebSocket
@ServerEndpoint("/meeting-online")
@Slf4j
public class OnlineMeetingSocket {


    private Session session;
    public static Vector<OnlineMeetingSocket> clients = new Vector<>();

    @OnOpen
    public void onOpen(Session session){
        this.session = session;
        clients.add(this);
        log.info("create a new session: {}",session.getId());
    }
    @OnClose
    public void onClose(Session session){
        clients.remove(this);
        log.info("close session: {}",session.getId());
    }
    @OnMessage
    public void onMessage(ByteBuffer bb, Session session){
        log.info("recive message from session: {}",session.getId());
        clients.stream().forEach(client->{
            try {
                client.session.getBasicRemote().sendBinary(bb);
            } catch (IOException e) {
                log.error("send msg error ...");
            }
        });
    }
    @OnMessage
    public void onMessage(String msg, Session session){
        log.info("recive message from session: {}",session.getId());
        clients.stream().forEach(client->{
            try {
                if(client != this){
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.error("send msg error ...");
            }
        });
    }

}
