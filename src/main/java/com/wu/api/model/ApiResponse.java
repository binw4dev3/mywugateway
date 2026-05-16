package com.wu.api.model;

/**
 * Generic API response envelope, consistent across all endpoints.
 * Mirrors the existing RespBean pattern but with type safety via generics.
 */
public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    private ApiResponse() {}

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(200, message, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(500, message, data);
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}
