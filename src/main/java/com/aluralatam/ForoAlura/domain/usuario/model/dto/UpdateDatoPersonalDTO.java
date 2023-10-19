package com.aluralatam.ForoAlura.domain.usuario.model.dto;
import jakarta.validation.constraints.*;
public record UpdateDatoPersonalDTO(
    @NotBlank
    @Pattern(
        regexp="^(?:[A-Z][a-z]{2,34}|[A-Z][a-z]+ [A-Z][a-z]+)$",
        message="Debe contener tres caracteres alfabeticos como minimo y" +
                "trinta y cinco como maximo. " +
                "Solo los nombres pueden iniciar con mayuscula, " +
                "esta no debe encontrarse en el medio de la palabra."
    )
    String nombre,
    @NotBlank
    @Pattern(
        regexp="^(?:[A-Z][a-z]{2,34}|[A-Z][a-z]+ [A-Z][a-z]+)$",
        message="Debe contener tres caracteres alfabeticos como minimo y " +
                "trinta y cinco como maximo. " +
                "Solo los apellidos pueden iniciar con mayuscula, " +
                "esta no debe encontrarse en el medio de la palabra."
    )
    String apellido,
    @NotBlank
    @Size(min=3,max=50)
    String provincia,
    @NotBlank
    @Size(min=2,max=50)
    String localidad
)
{}
