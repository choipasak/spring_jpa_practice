package com.study.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpaApplication {

	// 이게 내장 톰캣 서버를 가동 시켜주는 주문임
	public static void main(String[] args) {
		SpringApplication.run(JpaApplication.class, args);
	}
		// 톰캣을 돌리고 싶으면 이 main메서드를 돌려야함
}
