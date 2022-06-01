package com.macro.mall.tiny.common.api;

/**
 * 封装API的错误码
 *
 * @author Starbamboo
 * @date 6/1/2022 4:17 PM
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
