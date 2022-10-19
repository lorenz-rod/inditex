package com.example.inditex.exceptions;

public class HttpClientException extends Exception {

    private int code;
    private String message;

    public HttpClientException(int code, String message) {
        this.code = code;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
