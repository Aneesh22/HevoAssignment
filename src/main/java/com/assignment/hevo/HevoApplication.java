package com.assignment.hevo;

import com.assignment.hevo.handler.FileChangeDetectionHandler;
import com.assignment.hevo.service.FileService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class HevoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HevoApplication.class, args);
		try {
			FileService fileService = (FileService) context.getBean("searchService");
			FileChangeDetectionHandler handler = new FileChangeDetectionHandler(fileService);
			Thread t = new Thread(handler, "detectionHandler");
			t.start();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
