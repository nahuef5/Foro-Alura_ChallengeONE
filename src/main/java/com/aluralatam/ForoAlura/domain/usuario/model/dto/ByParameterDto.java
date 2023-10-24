package com.aluralatam.ForoAlura.domain.usuario.model.dto;
import jakarta.validation.constraints.NotBlank;
public record ByParameterDto(
        @NotBlank
        String parameter
)
{}