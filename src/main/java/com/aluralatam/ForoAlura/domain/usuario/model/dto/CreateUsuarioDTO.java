package com.aluralatam.ForoAlura.domain.usuario.model.dto;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
public record CreateUsuarioDTO(
    @NotNull
    @Valid
    CreateDatoPersonalDTO datosPersonales,
    @NotBlank
    @Email
    String email,
    @NotBlank
    @Pattern(
        regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#_\\-.$!&*?])[A-Za-z\\d#_\\-.$!&*?]{8,35}$",
        message ="Debe contener al menos: "+
                "una letra minuscula, " +
                "una letra mayuscula, " +
                "un dígito numerico, " +
                "uno de los siguientes caracteres especiales: #_-.$!&*?.\n" +
                "Y debe tener una longitud entre 8 y 35 caracteres"
    )
    String contrasena,
    @NotBlank
    String confirmarContrasena
)
{}