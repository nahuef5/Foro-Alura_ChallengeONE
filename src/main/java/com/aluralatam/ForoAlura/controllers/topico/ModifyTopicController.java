package com.aluralatam.ForoAlura.controllers.topico;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.ModifyTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.modifications.ModifyTopicoService;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.ByParameterDto;
import com.aluralatam.ForoAlura.global.exceptions.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/topico")
@RequiredArgsConstructor
public class ModifyTopicController {
    private final ModifyTopicoService service;
    @PutMapping("/editTopic/{id}")
    public ResponseEntity<Topico>editTopic(
            @PathVariable("id")@Valid Long id,
            @RequestBody ModifyTopicDto dto
            )throws ResourceNotFoundException
    {
        return service.editTopic(id,dto);
    }
    @PatchMapping("/changeTopicStatus/{id}")
    public ResponseEntity<Topico>changeTopicStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid ByParameterDto dto
    ) throws ResourceNotFoundException, BusinessRuleException {
        var status=dto.parameter();
        return service.changeTopicStatus(id,status);
    }
}