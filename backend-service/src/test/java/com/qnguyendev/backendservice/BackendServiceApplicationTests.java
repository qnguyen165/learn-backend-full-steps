package com.qnguyendev.backendservice;

import com.qnguyendev.backendservice.controller.AuthenticationController;
import com.qnguyendev.backendservice.controller.EmailController;
import com.qnguyendev.backendservice.controller.UserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendServiceApplicationTests {

	@InjectMocks
	private AuthenticationController authenticationController;

	@InjectMocks
	private UserController userController;

	@InjectMocks
	private EmailController emailController;

	@Test
	void contextLoads() {
		Assertions.assertNotNull(authenticationController);
		Assertions.assertNotNull(userController);
		Assertions.assertNotNull(emailController);
	}
}
