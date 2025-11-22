// src/main/java/com/amk/exception/BusinessException.java
package com.amk.exception;

import com.amk.utils.ResultCodeEnum;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ResultCodeEnum code;

    public BusinessException(ResultCodeEnum code) {
        super(code.getMessage());
        this.code = code;
    }

    public BusinessException(ResultCodeEnum code, String message) {
        super(message);
        this.code = code;
    }
}