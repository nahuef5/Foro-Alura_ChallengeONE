package com.aluralatam.ForoAlura.global.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BusinessRuleException extends Exception{
    public BusinessRuleException(String message){super(message);}}