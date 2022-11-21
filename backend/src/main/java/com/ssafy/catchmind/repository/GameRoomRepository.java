package com.ssafy.catchmind.repository;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.RoomStatusEnum;
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
        gameRoom.setStatus(GameStatusEnum.GAME_START);
        gameRoom.setRoomStatus(RoomStatusEnum.STAND_BY);
        gameRoom.setMaxNumOfPeople(gameRoomRequestDTO.getMaxNumOfPeople());
        gameRoom.setNowDrawer("");
        gameRoom.setAnswer("0a124856-2b15-459d-8d6d-145aabd29231");
        gameRoom.setNumOfPeople(0);
        gameRoom.setUserSet(new LinkedHashSet<>());
        gameRoom.getUserSet().add(gameRoomRequestDTO.getUser());
        gameRoom.setRoomMaster(gameRoomRequestDTO.getUser().getUserToken());
        gameRoomDTOMap.put(gameRoom.getRoomId(), gameRoom);

        return gameRoom;
    }

    public void removeGameRoom(String roomId) {
        gameRoomDTOMap.remove(roomId);
    }
}
