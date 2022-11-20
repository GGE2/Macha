package com.ssafy.catchmind.model.dto;

import com.ssafy.catchmind.model.GameStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

@Getter
@Setter
public class GameRoomDTO {

    LinkedHashSet<User> userSet;
    String roomId;
    Integer gameTime;
    String roomMaster;
    String roomName;
    String nowDrawer;
    Integer numOfPeople;
    GameStatusEnum status;
    Integer maxNumOfPeople;

    public static GameRoomDTO create(String roomName, Integer gameTime, User user, Integer maxNumOfPeople){
        GameRoomDTO room = new GameRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.roomName = roomName;
        room.gameTime = gameTime;
        room.numOfPeople = 1;
        room.roomMaster = user.getUserToken();
        room.maxNumOfPeople = maxNumOfPeople;
        room.setStatus(GameStatusEnum.READY);
        room.nowDrawer = user.getUserToken();
        room.setUserSet(new LinkedHashSet<>());
        room.getUserSet().add(user);
        return room;
    }
}
