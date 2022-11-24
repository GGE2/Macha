package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.GameStatusEnum;
import com.ssafy.catchmind.model.MessageTypeEnum;
import com.ssafy.catchmind.model.RoomStatusEnum;
import com.ssafy.catchmind.model.dto.*;
import com.ssafy.catchmind.model.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class StompGameRoomController {

    private final String[] answerArr = new String[] {
            "김정은", "피자", "푸우", "피카츄", "와플", "안드로이드", "마스터카드", "사자", "허태식 교수님",
            "제주도", "고양이", "바람개비", "인스타그램", "경찰서", "칫솔", "꿀벌", "라이터", "태풍", "히틀러",
            "사슴", "이어폰", "스티브 잡스", "프라이팬", "도깨비"
    };

    private final HashMap<String, TimerThread> threadMap = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SimpMessagingTemplate template; //특정 Broker로 메세지를 전달

    @Autowired
    private final RoomService roomService;

    ///game/room/enter/
    @MessageMapping(value = "/room/enter") // send.(/pub/chat/enter)
    public void enter(RoomEnterDTO roomEnterMsg){
        // 게임방 입장과 동시에 채팅방에도 입장
        logger.info("/room/enter : " + roomEnterMsg);
        GameRoomDTO gameRoom = roomService.findByRoomId(roomEnterMsg.getRoomId());
        gameRoom.getUserSet().add(roomEnterMsg.getUser());
        gameRoom.setNumOfPeople(gameRoom.getNumOfPeople() + 1);

        template.convertAndSend("/sub/game-room/" + roomEnterMsg.getRoomId(), gameRoom);

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setUser(roomEnterMsg.getUser());
        chatMessageDTO.setMessageType(MessageTypeEnum.NOTICE);
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
        logger.info("/room/exit/ " + roomEnterMsg);
        GameRoomDTO gameRoom = roomService.findByRoomId(roomEnterMsg.getRoomId());
        logger.info("room/exit/ roomDetail : " + gameRoom);
        LinkedHashSet<User> users = gameRoom.getUserSet();

        // 나간 사람이 방장이면 subscriber들에게 EXIT 메세지 보내줌
        if (roomEnterMsg.getUser().getUserToken().equals(gameRoom.getRoomMaster())) {
            users.remove(roomEnterMsg.getUser());
            gameRoom.setNumOfPeople(gameRoom.getNumOfPeople()-1);

            if (gameRoom.getNumOfPeople() == 0) {
                roomService.removeByRoomId(roomEnterMsg.getRoomId());
            } else {
                template.convertAndSend("/sub/game/status/" + gameRoom.getRoomId(), RoomStatusEnum.EXIT);
            }
            return;
        }

        if (gameRoom.getNumOfPeople() >= 1) {
            users.remove(roomEnterMsg.getUser());
            gameRoom.setNumOfPeople(gameRoom.getNumOfPeople() - 1);
            // 유저가 더이상 방에 없으면 방삭제
            if (gameRoom.getNumOfPeople() == 0) {
                roomService.removeByRoomId(roomEnterMsg.getRoomId());
                return;
            } else {
                logger.info("after exit : " + gameRoom);
                template.convertAndSend("/sub/game-room/" + roomEnterMsg.getRoomId(), gameRoom);
            }
        }
    }

    @MessageMapping("/game/round-start")
    public void roundStart(String roomId) {
        template.convertAndSend("/sub/canvas/clear/" + roomId, "CANVAS_CLEAR");
        logger.info("/game/round-start : " + roomId);
        GameRoomDTO gameRoomDTO = roomService.findByRoomId(roomId);

        LinkedHashSet<User> userSet = gameRoomDTO.getUserSet();
        User[] userArr = new User[userSet.size()];
        userArr = userSet.toArray(userArr);
        // 쓰레드를 돌려서 /sub/round-timer/
        // 쓰레드를 돌려서 타이머를 돌린다
        // 첫번째 라운드 시작이라면 유저 점수정보를 map에 넣어준다.

//        if (gameRoomDTO.getRoundCnt() == 0) {
//            ChatMessageDTO chatMessage = new ChatMessageDTO();
//            chatMessage.setUser(userArr[0]);
//            chatMessage.setMessageType(MessageTypeEnum.NOTICE);
//            chatMessage.setMessage("");
//            template.convertAndSend("/sub/chat-room/" + roomId, chatMessage);
//        }

        TimerThread timerThread = new TimerThread(gameRoomDTO.getGameTime(), gameRoomDTO.getRoomId(), gameRoomDTO.getRoundCnt());
        timerThread.start();

        threadMap.put(gameRoomDTO.getRoomId(), timerThread);
        gameRoomDTO.setStatus(GameStatusEnum.START_ROUND);
        gameRoomDTO.setRoomStatus(RoomStatusEnum.PROCEEDING);
        //

        if (gameRoomDTO.getScoreMap() == null) {
            HashMap<String, Integer> scoreMap = new HashMap<>();
            for (User user : userArr) {
                scoreMap.put(user.getUserToken(), 0);
            }
            gameRoomDTO.setScoreMap(scoreMap);
        }

        int idx = gameRoomDTO.getRoundCnt() % gameRoomDTO.getUserSet().size();
        gameRoomDTO.setNowDrawer(userArr[idx].getUserToken());
        gameRoomDTO.setRoundCnt(gameRoomDTO.getRoundCnt() + 1);
        gameRoomDTO.setAnswer(answerArr[(int) ((Math.random() * 1000) % answerArr.length)]);
        logger.info("/game/round-start - " + gameRoomDTO);
        template.convertAndSend("/sub/game-room/" + gameRoomDTO.getRoomId(), gameRoomDTO);
    }

    @MessageMapping("/game/round-end")
    public void roundEnd(String roomId) {
        GameRoomDTO gameRoomDTO = roomService.findByRoomId(roomId);

        LinkedHashSet<User> userSet = gameRoomDTO.getUserSet();
        User[] userArr = new User[userSet.size()];
        userArr = userSet.toArray(userArr);

        ChatMessageDTO chatMessage = new ChatMessageDTO();
        chatMessage.setMessageType(MessageTypeEnum.NOTICE);
        chatMessage.setMessage(gameRoomDTO.getRoundCnt() + "라운드의 정답은 " + gameRoomDTO.getAnswer() + "입니다.");
        chatMessage.setUser(userArr[0]);

        template.convertAndSend("/sub/chat-room/" + roomId, chatMessage);
        logger.info("/game/round-end - " + roomId);
        if (gameRoomDTO.getRoundCnt() >= gameRoomDTO.getUserSet().size()) {
            // 게임 끝 ~
            gameRoomDTO.setStatus(GameStatusEnum.END_GAME);
            gameRoomDTO.setRoomStatus(RoomStatusEnum.STAND_BY);
            gameRoomDTO.setRoundCnt(0); // 게임끝이니까 rountCnt초기화
            ArrayList<GameResultDTO> gameResultDTOList = new ArrayList<>();

            HashMap<String, Integer> scoreMap = gameRoomDTO.getScoreMap();
            HashMap<String, Integer> newScoreMap = new HashMap<>();

            User[] users = new User[userSet.size()];
            users = userSet.toArray(users);


            for (User user : users) {
                GameResultDTO gameResultDTO = new GameResultDTO(user, scoreMap.get(user.getUserToken()));
                gameResultDTOList.add(gameResultDTO);
                newScoreMap.put(user.getUserToken(), 0);
            }

            gameRoomDTO.setNowDrawer("");
            Collections.sort(gameResultDTOList);
            gameRoomDTO.setScoreMap(null);
            threadMap.get(gameRoomDTO.getRoomId()).stop();
            template.convertAndSend("/sub/game-room/end/" + gameRoomDTO.getRoomId(), gameResultDTOList);
            template.convertAndSend("/sub/game-room/" + gameRoomDTO.getRoomId(), gameRoomDTO);
            template.convertAndSend("/sub/canvas/clear/" + roomId, "CANVAS_CLEAR");
        } else {
            threadMap.get(gameRoomDTO.getRoomId()).stop();
            template.convertAndSend("/sub/canvas/clear/" + roomId, "CANVAS_CLEAR");
            roundStart(roomId);
        }
    }

    private class TimerThread extends Thread{
        int gameTime = 0;
        String roomId;
        Integer roundCnt = 0;
        public TimerThread(int gameTime, String roomId, Integer roundCnt) {
            this.gameTime = gameTime;
            this.roomId = roomId;
            this.roundCnt = roundCnt;
        }

        @SneakyThrows
        @Override
        public void run() {
            int time = gameTime;
            super.run();
            if (roundCnt == 0) {
                Thread.sleep(1800);
            }  else {
                Thread.sleep(900);
            }
            template.convertAndSend("/sub/game-room/timer/" + roomId, time);
            while(time > 0) {
                try {
                    Thread.sleep(1000);
                    time--;
                    template.convertAndSend("/sub/game-room/timer/" + roomId, time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

