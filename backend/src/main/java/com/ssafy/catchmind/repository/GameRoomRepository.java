package com.ssafy.catchmind.repository;

import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.User;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Repository
public class GameRoomRepository {

    private Map<String, GameRoomDTO> gameRoomDTOMap;

    @PostConstruct
    private void init(){
        gameRoomDTOMap = new LinkedHashMap<>();
    }

    public List<GameRoomDTO> findAllRooms(){
        //채팅방 생성 순서 최근 순으로 반환
        List<GameRoomDTO> result = new ArrayList<>(gameRoomDTOMap.values());
        Collections.reverse(result);

        return result;
    }

    public GameRoomDTO findRoomById(String id){
        return gameRoomDTOMap.get(id);
    }

    public GameRoomDTO createGameRoomDTO(String roomName, Integer gameTime, User user, Integer maxNumOfPeople){
        GameRoomDTO room = GameRoomDTO.create(roomName, gameTime, user, maxNumOfPeople);
        gameRoomDTOMap.put(room.getRoomId(), room);

        return room;
    }
}
