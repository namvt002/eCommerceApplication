package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
public class CreateUserRequest {

	@JsonProperty
	private String username;
	@JsonProperty
	@NotNull(message = "Password not null")
	private String password;

	@JsonProperty
	@NotNull(message = "Password confirmation not null")
	private String confirmPassword;
	public String getUsername() {
		return username;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
