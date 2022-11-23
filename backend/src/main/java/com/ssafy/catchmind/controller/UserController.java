package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.User;
import com.ssafy.catchmind.model.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/user")
@CrossOrigin("*")
public class UserController {

    @Autowired
    UserServiceImpl userService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @PostMapping("/login")
    @ApiOperation(value = "로그인 처리후 성공적으로 로그인 되었다면 HTTP STATUS CODE : 200", response = ResponseEntity.class)
    public ResponseEntity<?> login(@RequestBody User user) {

        if (user.getUserToken().equals("null"))
            return new ResponseEntity<>(false, HttpStatus.OK);

        User selected = userService.login(user.getUserToken());
        logger.info("/login - " + user);

        // 아직 회원가입 안된 경우
        if (selected == null) {
            userService.insert(user);
            selected = userService.login(user.getUserToken());
        }

        return new ResponseEntity<>(selected, HttpStatus.OK);
    }
}
