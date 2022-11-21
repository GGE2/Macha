package com.ssafy.catchmind.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameResultDTO implements Comparable<GameResultDTO>{

    User user;
    Integer score;

    @Override
    public int compareTo(GameResultDTO o) {
        return Integer.compare(o.score, this.score);
    }
}
