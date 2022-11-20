package com.ssafy.catchmind.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRoomRequestDTO {

    User user;
    String roomName;
    Integer gameTime;
    Integer maxNumOfPeople;
}
