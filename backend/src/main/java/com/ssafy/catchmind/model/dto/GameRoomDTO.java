package com.ssafy.catchmind.model.dto;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.RoomStatusEnum;
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
    RoomStatusEnum roomStatus;
    Integer maxNumOfPeople;
    Integer roundCnt;
    String answer;
    HashMap<String, Integer> scoreMap;

    @Override
    public String toString() {
        return "GameRoomDTO{" +
                "userSet=" + userSet +
                ", roomId='" + roomId + '\'' +
                ", gameTime=" + gameTime +
                ", roomMaster='" + roomMaster + '\'' +
                ", roomName='" + roomName + '\'' +
                ", nowDrawer='" + nowDrawer + '\'' +
                ", numOfPeople=" + numOfPeople +
                ", status=" + status +
                ", roomStatus=" + roomStatus +
                ", maxNumOfPeople=" + maxNumOfPeople +
                ", roundCnt=" + roundCnt +
                ", answer='" + answer + '\'' +
                ", scoreMap=" + scoreMap +
                '}';
    }
}
