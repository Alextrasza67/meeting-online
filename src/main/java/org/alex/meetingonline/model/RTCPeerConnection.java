package org.alex.meetingonline.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RTCPeerConnection {

    private User holder;
    private User user;

    private String holderIce;
    private String userIce;
}
