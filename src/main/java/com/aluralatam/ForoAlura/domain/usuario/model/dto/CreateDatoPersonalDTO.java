package com.aluralatam.ForoAlura.domain.usuario.model.dto;

import com.aluralatam.ForoAlura.global.tools.validations.FechaNacimientoValid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record CreateDatoPersonalDTO(
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
    @Past
    @FechaNacimientoValid
    LocalDate fechaNacimiento,
    @NotBlank
    @Size(min=4, max=50)
    String pais,
    @NotBlank
    @Size(min=3,max=50)
    String provincia,
    @NotBlank
    @Size(min=2,max=50)
    String localidad
)
{}