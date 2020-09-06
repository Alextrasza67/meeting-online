package com.gitlab.alex.meetingonline.config;

import org.apache.catalina.Context;
import org.apache.tomcat.websocket.server.WsSci;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig /*implements WebSocketConfigurer*/ {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
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
