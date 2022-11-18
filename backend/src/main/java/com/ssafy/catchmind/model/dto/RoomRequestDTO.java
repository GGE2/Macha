package com.ssafy.catchmind.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequestDTO {

    String roomName;
    int gameTime;
    String roomMasterToken;
}
