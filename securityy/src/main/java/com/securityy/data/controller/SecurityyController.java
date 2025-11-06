package com.securityy.data.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/securityy")
public class SecurityyController {
	
	
	
	@GetMapping("/hello")
	public String sayHello() {
		return "Enrty point";
	}

	
	@GetMapping("/user")
    public String userArea() {
        return "User area: accessible to USER or ADMIN";
    }

    @GetMapping("/admin")
    public String adminArea() {
        return "Admin area: accessible to ADMIN only";
    }
}
