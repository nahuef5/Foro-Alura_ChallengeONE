package com.aluralatam.ForoAlura.global.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotConfirmedException extends RuntimeException{
    public NotConfirmedException(String message){
        super(message);
    }
}