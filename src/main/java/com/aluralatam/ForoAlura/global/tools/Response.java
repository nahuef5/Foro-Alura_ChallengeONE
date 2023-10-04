package com.aluralatam.ForoAlura.global.tools;
import lombok.*;
import org.springframework.http.HttpStatus;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Response{
    private HttpStatus httpStatus;
    private String respuesta;
    public static final String NO_ID_EXISTS="NO EXISTE UN RECURSO CON ESE ID.";
    public static final String NO_PARAMETER_EXIST="ESE RECURSO NO EXISTE.";
    public static final String ALREADY_EXIST="EXISTE UN RECURSO CON ESAS PROPIEDADES.";
    public static final String EMPTY_LIST="POR EL MOMENTO NO TENEMOS RECURSOS DISPONIBLES.";
    public static final String CREATED="RECURSO CREADO EXITOSAMENTE.";
    public static final String UPDATED="RECURSO MODIFICADO EXITOSAMENTE.";
    public static final String NOT_CONFIRMED="RECURSO NO MODIFICADO.";
    public static final String ELIMINATED="RECURSO ELIMINADO EXITOSAMENTE.";
}