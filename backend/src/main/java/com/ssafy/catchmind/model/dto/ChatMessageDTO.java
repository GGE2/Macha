package com.ssafy.catchmind.model.dto;

import com.ssafy.catchmind.model.MessageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private String roomId;
    private User user;
    private String message;
    private MessageTypeEnum messageType;

    @Override
    public String toString() {
        return "ChatMessageDTO{" +
                "roomId='" + roomId + '\'' +
                ", user=" + user +
                ", message='" + message + '\'' +
                ", messageType=" + messageType +
                '}';
    }
}