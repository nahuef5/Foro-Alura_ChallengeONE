package com.aluralatam.ForoAlura.global.exceptions;
import com.aluralatam.ForoAlura.global.tools.ExceptionsTool;
import com.aluralatam.ForoAlura.global.tools.Response;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptions{
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Response> throwResourceNotFound
            (ResourceNotFoundException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new Response(HttpStatus.NOT_FOUND, e.getMessage()));
    }
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Response>throwEntityAlreadyExists
            (EntityAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new Response(HttpStatus.CONFLICT, e.getMessage()));
    }
    @ExceptionHandler(EmptyEntityListException.class)
    public ResponseEntity<Response> throwEmptyEntityList(
            EmptyEntityListException e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
    }
    @ExceptionHandler(NotConfirmedException.class)
    public ResponseEntity<Response> throwNotConfirm(
            NotConfirmedException e){
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new Response(HttpStatus.NOT_IMPLEMENTED, e.getMessage()));
    }
    @ExceptionHandler(AccountActivationException.class)
    public ResponseEntity<Response> throwNotAccountActivation(
            AccountActivationException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    @ExceptionHandler(IsNotPositiveException.class)
    public ResponseEntity<Response> throwInvalidValuesException(
            IsNotPositiveException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Response> throwIsNotNumberException(
            NumberFormatException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> throwIllegalArgumentException(
            IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(HttpStatus.BAD_REQUEST, e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response>throwMethodArgumentNotValid
            (MethodArgumentNotValidException e){

        List<String> messages= ExceptionsTool.errorList(e);
        var message=ExceptionsTool.trimBrackets(messages);
        return ResponseEntity.badRequest()
                .body(new Response(HttpStatus.BAD_REQUEST, message));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> throwException(Exception e){
        return ResponseEntity.internalServerError()
                .body(new Response(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage()));
    }
}