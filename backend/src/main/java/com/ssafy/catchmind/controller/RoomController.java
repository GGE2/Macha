package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.RoomRequestDTO;
import com.ssafy.catchmind.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class RoomController {

    private final ChatRoomRepository repository;

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public ResponseEntity<?> rooms(){

        log.info("# All Chat Rooms");;

        return new ResponseEntity<>(repository.findAllRooms(), HttpStatus.OK);
    }

    //채팅방 개설
    @PostMapping(value = "/room")
    public ResponseEntity<?> create(@RequestBody RoomRequestDTO roomRequestDTO){

        log.info("# Create Chat Room , name: " + roomRequestDTO.getRoomName());

        return new ResponseEntity<>(repository.createChatRoomDTO(roomRequestDTO.getRoomMasterToken()), HttpStatus.OK);
    }

    //채팅방 조회
    @GetMapping("/room")
    public ResponseEntity<?> getRoom(@RequestParam String roomId){

        log.info("# get Chat Room, roomID : " + roomId);

        return new ResponseEntity<>(repository.findRoomById(roomId), HttpStatus.OK);
    }
}
