package com.ssafy.catchmind;

import com.ssafy.catchmind.model.dao.UserDao;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackageClasses = UserDao.class)
public class CatchmindApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatchmindApplication.class, args);
	}

}
