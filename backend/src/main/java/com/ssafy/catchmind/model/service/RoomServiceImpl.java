package com.ssafy.catchmind.model.service;

import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
import com.ssafy.catchmind.repository.GameRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Autowired
    private final GameRoomRepository gameRoomRepository;

    public GameRoomDTO createGameRoom(GameRoomRequestDTO gameRoomRequestDTO) {
        return gameRoomRepository.insertGameRoom(gameRoomRequestDTO);
    }

    public List<GameRoomDTO> findAllGameRooms() {
        return gameRoomRepository.findAllRooms();
    }

    public GameRoomDTO findByRoomId(String roomId) {
        return gameRoomRepository.findRoomById(roomId);
    }

    public void removeByRoomId(String roomId) {
        gameRoomRepository.removeGameRoom(roomId);
    }


}
