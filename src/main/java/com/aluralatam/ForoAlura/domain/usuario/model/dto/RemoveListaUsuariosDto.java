package com.aluralatam.ForoAlura.domain.usuario.model.dto;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
public record RemoveListaUsuariosDto(
    @NotEmpty
    List<Long> ids,
    @NotNull(message="No debe ser nulo.")
    boolean remove
)
{}
