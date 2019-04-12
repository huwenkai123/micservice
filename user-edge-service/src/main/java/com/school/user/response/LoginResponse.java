package com.school.user.response;

public class LoginResponse extends Response {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginResponse(String token) {
        this.token = token;
    }

}
