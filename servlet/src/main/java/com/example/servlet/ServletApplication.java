package com.example.servlet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ServletApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServletApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<MyServlet> myServlet() {
		return new ServletRegistrationBean<>(new MyServlet(), "/my-servlet");
	}
}
