package com.ssafy.catchmind.model.service;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
import com.ssafy.catchmind.repository.GameRoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameServiceImpl implements GameService{

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
}
