package com.aluralatam.ForoAlura.domain.topico.model.dtos;
import jakarta.validation.constraints.*;
public record CreateTopicDto(
        @NotBlank
        @Size(min=4,max=65)
        String titulo,
        @NotBlank
        @Size(min=4,max=5000)
        String mensaje,
        @NotNull
        @Min(0)
        Long usuario_id,
        @NotNull
        @Min(0)
        Long curso_id
){}