package com.ssafy.catchmind.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    public ChatMessageDTO(String roomId, User user, String message) {
        this.roomId = roomId;
        this.user = user;
        this.message = message;
    }

    private String roomId;
    private User user;
    private String message;

}