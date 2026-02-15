package org.example.testmentorbackend.dto;

public class ErrorResponseDto {
    private int statusCode;
    private String message;

    public ErrorResponseDto(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}