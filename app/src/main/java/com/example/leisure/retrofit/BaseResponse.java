package com.example.leisure.retrofit;

/**
 * 统一响应
 *
 * @param <T>
 */
public class BaseResponse<T> {
    public String code;
    public String message;
//    public T list;
    public T result;
    public T data;
}
