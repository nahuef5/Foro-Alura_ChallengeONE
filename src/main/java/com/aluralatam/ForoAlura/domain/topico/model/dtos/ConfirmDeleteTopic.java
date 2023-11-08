package com.aluralatam.ForoAlura.domain.topico.model.dtos;

import jakarta.validation.constraints.*;

public record ConfirmDeleteTopic(
        @Min(0)
        Long id,
        @NotNull
        boolean confirm
){}