package com.ssafy.catchmind.model.service;

import com.ssafy.catchmind.model.dto.User;

public interface UserService {

    public User login(String token);
    public int insert(User user);
}
