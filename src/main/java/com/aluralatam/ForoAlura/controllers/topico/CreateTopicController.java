package com.aluralatam.ForoAlura.controllers.topico;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.CreateTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.create.CreateTopicoService;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/topico")
@RequiredArgsConstructor
public class CreateTopicController {
    private final CreateTopicoService service;
    @PostMapping("/ask")
    public ResponseEntity<Topico>save(@RequestBody CreateTopicDto dto)
            throws ResourceNotFoundException
    {
        return service.createTopic(dto);
    }
}