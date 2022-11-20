package com.ssafy.catchmind.controller;

import com.ssafy.catchmind.model.dto.User;
import com.ssafy.catchmind.model.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @PostMapping("/login")
    @ApiOperation(value = "로그인 처리후 성공적으로 로그인 되었다면 HTTP STATUS CODE : 200", response = ResponseEntity.class)
    public ResponseEntity<?> login(@RequestBody User user) {

        if (user.getUserToken().equals("null"))
            return new ResponseEntity<>(false, HttpStatus.OK);

        User selected = userService.login(user.getUserToken());
        System.out.println(user);

        // 아직 회원가입 안된 경우
        if (selected == null) {
            userService.insert(user);
            selected = userService.login(user.getUserToken());
        }

        return new ResponseEntity<>(selected, HttpStatus.OK);
    }

    @GetMapping("/test")
    public String test(@RequestParam String str) {
        System.out.println(str);
        System.out.println();
        return "success";
    }
}
