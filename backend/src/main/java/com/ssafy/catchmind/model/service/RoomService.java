package com.ssafy.catchmind.model.service;

import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;

import java.util.List;

public interface RoomService {

    GameRoomDTO createGameRoom(GameRoomRequestDTO gameRoomRequestDTO);
    List<GameRoomDTO> findAllGameRooms();
    GameRoomDTO findByRoomId(String roomId);
    void removeByRoomId(String roomId);

}
