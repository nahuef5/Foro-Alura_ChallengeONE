package com.aluralatam.ForoAlura.controllers.topico;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.ConfirmDeleteTopic;
import com.aluralatam.ForoAlura.domain.topico.service.delete.DeleteTopicoService;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/topico")
@RequiredArgsConstructor
public class DeleteTopicController {
    private final DeleteTopicoService service;
    @DeleteMapping("/delete")
    public ResponseEntity<Response>deleteFromDDBB(@RequestBody ConfirmDeleteTopic dto)
            throws BusinessRuleException, ResourceNotFoundException
    {
        return service.deleteTopicFromDDBB(dto);
    }
}