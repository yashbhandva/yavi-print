package com.web_app.yaviPrint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class YaviPrintApplication {
	public static void main(String[] args) {
		SpringApplication.run(YaviPrintApplication.class, args);
	}
}