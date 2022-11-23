package com.ssafy.catchmind.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomEnterDTO {

    String roomId;
    User user;

    @Override
    public String toString() {
        return "RoomEnterDTO{" +
                "roomId='" + roomId + '\'' +
                ", user=" + user +
                '}';
    }
}
