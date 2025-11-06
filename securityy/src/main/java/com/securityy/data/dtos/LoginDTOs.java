package com.securityy.data.dtos;

public class LoginDTOs {
	
	public record LoginRequest(String username, String password) {}
	public record LoginResponse(String accessToken, long expiresInSeconds) {}

}
