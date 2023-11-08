package com.aluralatam.ForoAlura.domain.topico.model.dtos;
import jakarta.validation.constraints.*;
public record ModifyTopicDto(
    @NotBlank
    @Size(min=4,max=65)
    String titulo,
    @NotBlank
    @Size(min=4,max=5000)
    String mensaje
){}