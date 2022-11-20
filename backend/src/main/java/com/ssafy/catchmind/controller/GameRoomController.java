package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
import com.ssafy.catchmind.model.service.GameService;
import com.ssafy.catchmind.model.service.GameServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/game")
@Log4j2
public class GameRoomController {

    @Autowired
    private final GameService gameService;

    //게임방 목록 조회
    @GetMapping(value = "/rooms")
    public ResponseEntity<?> rooms(){

        log.info("# All Chat Rooms");;

        return new ResponseEntity<>(gameService.findAllGameRooms(), HttpStatus.OK);
    }

    //게임방 개설
    @PostMapping(value = "/room")
    public ResponseEntity<?> create(@RequestBody GameRoomRequestDTO roomRequestDTO){

        log.info("# Create Chat Room , name: " + roomRequestDTO.getUser().getNickname());
        return new ResponseEntity<>(gameService.createGameRoom(roomRequestDTO), HttpStatus.OK);
    }

    //게임방 조회
    @GetMapping("/room")
    public ResponseEntity<?> getRoom(@RequestParam String roomId){

        log.info("# get Chat Room, roomID : " + roomId);

        return new ResponseEntity<>(gameService.findByRoomId(roomId), HttpStatus.OK);
    }
}