package com.aluralatam.ForoAlura.global.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.CONFLICT)
public class EntityAlreadyExistsException extends Exception{
    public EntityAlreadyExistsException(String message){
        super(message);
    }
}