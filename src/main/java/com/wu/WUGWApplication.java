package com.wu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.wu.gw.mybatis.mapper")
@MapperScan(basePackages = "com.wu.web.mapper")
public class WUGWApplication {

	public static void main(String... args) {
		SpringApplication.run(WUGWApplication.class, args);
	}
}
