package com.aluralatam.ForoAlura.domain.curso.models.dtos;
import jakarta.validation.constraints.*;
public record NombreDto(
        @NotBlank
        @Size(min=3, max=30)
        @Pattern(regexp = "^[a-zA-Z]+$")
        String nombre
){}