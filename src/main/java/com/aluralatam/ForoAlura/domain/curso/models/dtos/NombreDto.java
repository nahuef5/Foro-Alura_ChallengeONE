package com.aluralatam.ForoAlura.domain.curso.models.dtos;
import jakarta.validation.constraints.*;
public record NombreDto(
        @NotBlank
        @Size(min=4, max=30)
        String nombre
){}