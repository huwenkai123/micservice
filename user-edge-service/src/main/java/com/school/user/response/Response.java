package com.school.user.response;

import java.io.Serializable;

public class Response implements Serializable {
    public static final Response USERNAME_PASSWORD_INALID = new Response("1001", "username or password is error");
    public static final Response MOBILE_OR_EMAIL_REQUIREO = new Response("1002", "username or password is requireo");
    public static final Response MOBILE_VERICODE = new Response("1003", "send, faild");
    public static final Response VERIFY_CODE_INVALID = new Response("1004", "verify code invalid");
    public static final Response SUCCESS = new Response();

    public static Response expection(Exception e) {
        return new Response("9999", e.getMessage());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String code;
    private String message;

    public Response() {
        this.code = "0";
        this.message = "success";
    }
    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
