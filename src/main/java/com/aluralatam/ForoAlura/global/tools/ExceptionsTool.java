package com.aluralatam.ForoAlura.global.tools;
import org.springframework.validation.BindException;
import java.util.*;
public class ExceptionsTool{
    public static List<String> errorList(BindException e){
        List<String> messages=new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach(
                (error)->{
                    messages.add(error.getDefaultMessage());
                });
        return messages;
    }
    public static String trimBrackets(List<String> messages){
        String message=messages.toString();
        System.out.println(message);
        return message.replaceAll("[\\[\\]]", "");
    }
}