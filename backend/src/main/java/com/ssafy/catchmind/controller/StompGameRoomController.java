package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.RoomStatusEnum;
import com.ssafy.catchmind.model.dto.*;
import com.ssafy.catchmind.model.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class StompGameRoomController {

    private final String[] answerArr = new String[] {
            "코끼리", "기린", "사자", "사과", "안경"
    };

    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    @Autowired
    private final RoomService roomService;

    ///game/room/enter/
    @MessageMapping(value = "/room/enter") // send.(/pub/chat/enter)
    public void enter(RoomEnterDTO roomEnterMsg){
        // 게임방 입장과 동시에 채팅방에도 입장
        GameRoomDTO gameRoom = roomService.findByRoomId(roomEnterMsg.getRoomId());
        gameRoom.getUserSet().add(roomEnterMsg.getUser());
        gameRoom.setNumOfPeople(gameRoom.getNumOfPeople() + 1);

        template.convertAndSend("/sub/game-room/" + roomEnterMsg.getRoomId(), gameRoom);

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setUser(roomEnterMsg.getUser());
        chatMessageDTO.setMessage(roomEnterMsg.getUser().getNickname() + " 님이 게임에 입장했습니다.");
        chatMessageDTO.setRoomId(roomEnterMsg.getRoomId());

        template.convertAndSend("/sub/chat-room/" + roomEnterMsg.getRoomId(), chatMessageDTO);
    }

//    @MessageMapping(value = "/game/message")
//    public void message(ChatMessageDTO message){
//        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
//    }

    @MessageMapping(value = "/room/exit")
    public void roomExit(RoomEnterDTO roomEnterMsg) {
        // 유저가 게임방 나가면 게임방에서 제거해줌
        GameRoomDTO gameRoom = roomService.findByRoomId(roomEnterMsg.getRoomId());
        LinkedHashSet<User> users = gameRoom.getUserSet();

        // 나간 사람이 방장이면 subscriber들에게 EXIT 메세지 보내줌
        if (roomEnterMsg.getUser().getUserToken().equals(gameRoom.getRoomMaster())) {
            users.remove(roomEnterMsg.getUser());
            gameRoom.setNumOfPeople(gameRoom.getNumOfPeople()-1);
            // sub
            if (gameRoom.getNumOfPeople() == 0) {
                roomService.removeByRoomId(roomEnterMsg.getRoomId());
                return;
            }

            template.convertAndSend("/sub/game/status", RoomStatusEnum.EXIT);
            return;
        }

        if (gameRoom.getNumOfPeople() > 1) {
            users.remove(roomEnterMsg.getUser());
            gameRoom.setNumOfPeople(gameRoom.getNumOfPeople() - 1);
            // 유저가 더이상 방에 없으면 방삭제
            if (gameRoom.getNumOfPeople() == 0) {
                roomService.removeByRoomId(roomEnterMsg.getRoomId());
                return;
            } else {
                template.convertAndSend("/sub/game-room/" + roomEnterMsg.getRoomId(), gameRoom);
            }
        }
    }

    @MessageMapping("/game/round-start")
    public void roundStart(String roomId) {
        GameRoomDTO gameRoomDTO = roomService.findByRoomId(roomId);

        gameRoomDTO.setStatus(GameStatusEnum.START_ROUND);
        gameRoomDTO.setRoomStatus(RoomStatusEnum.PROCEEDING);
        //
        User[] userList = (User[])gameRoomDTO.getUserSet().toArray();
        int idx = gameRoomDTO.getRoundCnt() % gameRoomDTO.getUserSet().size();
        HashMap<String, Integer> scoreMap = gameRoomDTO.getScoreMap();

        for (int i = 0; i < userList.length; i++) {
            scoreMap.put(userList[i].getUserToken(), 0);
        }

        gameRoomDTO.setNowDrawer(userList[idx].getUserToken());
        gameRoomDTO.setRoundCnt(gameRoomDTO.getRoundCnt() + 1);
        gameRoomDTO.setAnswer(answerArr[(int) ((Math.random() * 1000) % answerArr.length)]);
        template.convertAndSend("/sub/game-room/" + gameRoomDTO.getRoomId(), gameRoomDTO);
    }

    @MessageMapping("/game/round-end")
    public void roundEnd(String roomId) {
        GameRoomDTO gameRoomDTO = roomService.findByRoomId(roomId);

        if (gameRoomDTO.getRoundCnt() == gameRoomDTO.getUserSet().size()) {
            // 게임 끝 ~
            gameRoomDTO.setStatus(GameStatusEnum.END_GAME);
            ArrayList<GameResultDTO> gameResultDTOList = new ArrayList<>();

            HashMap<String, Integer> scoreMap = gameRoomDTO.getScoreMap();
            LinkedHashSet<User> userSet = gameRoomDTO.getUserSet();
            User[] users = (User[])userSet.toArray();

            for (User user : users) {
                GameResultDTO gameResultDTO = new GameResultDTO(user, scoreMap.get(user.getUserToken()));
                gameResultDTOList.add(gameResultDTO);
            }

            Collections.sort(gameResultDTOList);
            template.convertAndSend("/sub/game-room/end" + gameRoomDTO.getRoomId(), gameResultDTOList);
            template.convertAndSend("/sub/game-room/" + gameRoomDTO.getRoomId(), gameRoomDTO);
        } else {
            // ROUND_START send
            roundStart(roomId);
        }
    }
}
