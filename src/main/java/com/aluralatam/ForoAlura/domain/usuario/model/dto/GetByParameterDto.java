package com.aluralatam.ForoAlura.domain.usuario.model.dto;
import jakarta.validation.constraints.NotBlank;
public record GetByParameterDto(
        @NotBlank
        String parameter
)
{}