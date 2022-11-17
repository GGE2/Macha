package com.ssafy.catchmind.model.service;

import com.ssafy.catchmind.model.dao.UserDao;
import com.ssafy.catchmind.model.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Override
    public User login(String token) {
        return userDao.select(token);
    }

    @Override
    public int insert(User user) {
        return userDao.insert(user);
    }
}
