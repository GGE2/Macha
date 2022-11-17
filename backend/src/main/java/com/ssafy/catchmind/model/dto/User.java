package com.ssafy.catchmind.model.dto;

import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@NoArgsConstructor
public class User {
    private Integer userId;
    private String userToken;
    private String profileImg;
    private String nickname;
    private Integer isOnline;

    @Builder
    public User(Integer userId, String userToken, String profileImg, String nickname, Integer isOnline) {
        super();
        this.userId = userId;
        this.userToken = userToken;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.isOnline = isOnline;
    }


}