package com.ssafy.catchmind.repository;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
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

    public GameRoomDTO insertGameRoom(GameRoomRequestDTO gameRoomRequestDTO){
        GameRoomDTO gameRoom = new GameRoomDTO();

        gameRoom.setRoomId(UUID.randomUUID().toString());
        gameRoom.setRoomName(gameRoomRequestDTO.getRoomName());
        gameRoom.setGameTime(gameRoomRequestDTO.getGameTime());
        gameRoom.setStatus(GameStatusEnum.READY);
        gameRoom.setMaxNumOfPeople(gameRoomRequestDTO.getMaxNumOfPeople());
        gameRoom.setNowDrawer(gameRoomRequestDTO.getUser().getUserToken());
        gameRoom.setNumOfPeople(1);
        gameRoom.setUserSet(new LinkedHashSet<>());
        gameRoom.setRoomMaster(gameRoomRequestDTO.getUser().getUserToken());
        gameRoomDTOMap.put(gameRoom.getRoomId(), gameRoom);

        return gameRoom;
    }
}
