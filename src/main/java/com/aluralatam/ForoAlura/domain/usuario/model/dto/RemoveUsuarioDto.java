package com.aluralatam.ForoAlura.domain.usuario.model.dto;

import jakarta.validation.constraints.*;

public record RemoveUsuarioDto(
        @Min(0)
        Long id,
        @NotNull(message="No debe ser nulo.")
        boolean remove
)
{}