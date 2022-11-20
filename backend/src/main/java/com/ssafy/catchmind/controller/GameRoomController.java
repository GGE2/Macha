package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
import com.ssafy.catchmind.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/game")
@Log4j2
public class GameRoomController {

    private final GameRoomRepository repository;

    //게임방 목록 조회
    @GetMapping(value = "/rooms")
    public ResponseEntity<?> rooms(){

        log.info("# All Chat Rooms");;

        return new ResponseEntity<>(repository.findAllRooms(), HttpStatus.OK);
    }

    //게임방 개설
    @PostMapping(value = "/room")
    public ResponseEntity<?> create(@RequestBody GameRoomRequestDTO roomRequestDTO){

        log.info("# Create Chat Room , name: " + roomRequestDTO.getUser().getNickname());

        return new ResponseEntity<>(
                repository.createGameRoomDTO(roomRequestDTO.getRoomName(),
                        roomRequestDTO.getGameTime(), roomRequestDTO.getUser(), roomRequestDTO.getMaxNumOfPeople()), HttpStatus.OK);
    }

    //게임방 조회
    @GetMapping("/room")
    public ResponseEntity<?> getRoom(@RequestParam String roomId){

        log.info("# get Chat Room, roomID : " + roomId);

        return new ResponseEntity<>(repository.findRoomById(roomId), HttpStatus.OK);
    }
}