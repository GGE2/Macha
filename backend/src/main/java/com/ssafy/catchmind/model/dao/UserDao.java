package com.ssafy.catchmind.model.dao;

import com.ssafy.catchmind.model.dto.User;

public interface UserDao {

    int insert(User user);
    User select(String userId);
    int update(User user);
    int delete(String token);
}
