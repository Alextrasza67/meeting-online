package org.alex.meetingonline.config;

import org.alex.meetingonline.model.Authentication;
import org.alex.meetingonline.model.Room;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer /*implements WebSocketConfigurer*/ {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/meeting").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.enableSimpleBroker("/user");
        config.setUserDestinationPrefix("/user/");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                //1. 判断是否首次连接请求
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    //2. 验证信息
                    String roomId = accessor.getNativeHeader("roomId").get(0);
                    String username = accessor.getNativeHeader("username").get(0);
                    String password = accessor.getNativeHeader("password").get(0);

                    Room room = RoomConfig.getRoom(roomId);
                    if(room == null){
                        throw new RuntimeException("websocket认证失败");
                    }
                    if(RoomConfig.checkUser(room, username, password)){
                        Authentication user = new Authentication(username); // access authentication header(s)}
                        accessor.setUser(user);
                        return message;
                    }
                    throw new RuntimeException("websocket认证失败");
                }

                //不是首次连接，已经成功登陆
                return message;
            }
        });
    }

//    /**
//     * 创建wss协议接口
//     * @return
//     */
//    @Bean
//    public TomcatContextCustomizer tomcatContextCustomizer() {
//        System.out.println("TOMCATCONTEXTCUSTOMIZER INITILIZED");
//        return new TomcatContextCustomizer() {
//            @Override
//            public void customize(Context context) {
//                context.addServletContainerInitializer(new WsSci(), null);
//            }
//
//        };
//    }

//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
//        webSocketHandlerRegistry.addHandler()
//    }
}
