package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.*;
import com.ssafy.catchmind.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class StompGameRoomController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    @Autowired
    private final GameRoomRepository gameRoomRepository;

    @MessageMapping(value = "/game/enter/") // send.(/pub/chat/enter)
    public void enter(RoomEnterDTO roomEnterMsg){
        // 게임방 입장과 동시에 채팅방에도 입장
        GameRoomDTO gameRoom = gameRoomRepository.findRoomById(roomEnterMsg.getRoomId());
        gameRoom.getUserSet().add(roomEnterMsg.getUser());
        gameRoom.setNumOfPeople(gameRoom.getNumOfPeople() + 1);

        String roomId = roomEnterMsg.getRoomId();
        User user = roomEnterMsg.getUser();
        //template.convertAndSend("/sub/chat/room/" + roomEnterMsg.getRoomId(), roomEnterMsg.getUser().getNickname() + " 님이 입장했습니다.");
        template.convertAndSend("/sub/game/room/" + roomEnterMsg.getRoomId(), gameRoom);
        template.convertAndSend("pub/chat/enter", new ChatMessageDTO(roomId, user, ""));
    }

//    @MessageMapping(value = "/game/message")
//    public void message(ChatMessageDTO message){
//        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }

}
