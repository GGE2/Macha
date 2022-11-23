package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.dto.GameRoomRequestDTO;
import com.ssafy.catchmind.model.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/game")
public class GameRoomController {

    @Autowired
    private final RoomService roomService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //게임방 목록 조회
    @GetMapping(value = "/rooms")
    public ResponseEntity<?> rooms(){

        logger.info("# All Chat Rooms");;

        return new ResponseEntity<>(roomService.findAllGameRooms(), HttpStatus.OK);
    }

    //게임방 개설
    @PostMapping(value = "/room")
    public ResponseEntity<?> create(@RequestBody GameRoomRequestDTO roomRequestDTO){

        logger.info("# Create Chat Room , name: " + roomRequestDTO.getUser().getNickname());
        return new ResponseEntity<>(roomService.createGameRoom(roomRequestDTO), HttpStatus.OK);
    }

    //게임방 조회
    @GetMapping("/room")
    public ResponseEntity<?> getRoom(@RequestParam String roomId){


        logger.info("# get Chat Room, roomID : " + roomId);
        GameRoomDTO gameRoomDTO = null;
        gameRoomDTO = roomService.findByRoomId(roomId);
        return new ResponseEntity<>(gameRoomDTO, HttpStatus.OK);
    }
}