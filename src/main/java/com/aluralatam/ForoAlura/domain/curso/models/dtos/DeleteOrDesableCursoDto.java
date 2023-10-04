package com.aluralatam.ForoAlura.domain.curso.models.dtos;
import jakarta.validation.constraints.*;
public record DeleteOrDesableCursoDto(
        @NotNull
        @Min(0)
        Long id,
        @NotNull
        boolean confirm
){}