package com.bank.simulator.exception;

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//public class PinNotValidException extends Exception {
//
//}

public class PinNotValidException extends Exception{

    public PinNotValidException(String message) {
        super(message);
    }
}
