package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.MessageTypeEnum;
import com.ssafy.catchmind.model.RoomStatusEnum;
import com.ssafy.catchmind.model.dto.ChatMessageDTO;
import com.ssafy.catchmind.model.dto.GameRoomDTO;
import com.ssafy.catchmind.model.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달
    private final RoomService roomService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @MessageMapping(value = "/chat/message")  //   pub/chat/message, {}, {}
    public void message(ChatMessageDTO message){
        GameRoomDTO gameRoomDTO = roomService.findByRoomId(message.getRoomId());

        if (gameRoomDTO.getAnswer().equals(message.getMessage()) &&
            !gameRoomDTO.getNowDrawer().equals(message.getUser().getUserToken())) {
            HashMap<String, Integer> scoreMap = gameRoomDTO.getScoreMap();
            String userToken = message.getUser().getUserToken();
            scoreMap.put(userToken, scoreMap.get(userToken) + 1);
            message.setMessageType(MessageTypeEnum.NOTICE);
            message.setMessage(message.getUser().getNickname() + "님이 정답을 맞췄습니다!!!");
            template.convertAndSend("/sub/game/status/" + message.getRoomId(), RoomStatusEnum.ANSWER);
            logger.info("/chat/message answer : " + gameRoomDTO);

            template.convertAndSend("/sub/chat-room/" + message.getRoomId(), message);
        } else {
            message.setMessageType(MessageTypeEnum.USER_CHAT);
            template.convertAndSend("/sub/chat-room/" + message.getRoomId(), message);
        }
    }
}
