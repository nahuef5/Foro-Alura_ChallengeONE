package com.aluralatam.ForoAlura.global.tools;
import lombok.*;
import org.springframework.http.HttpStatus;
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Response{
    private HttpStatus httpStatus;
    private String respuesta;
}