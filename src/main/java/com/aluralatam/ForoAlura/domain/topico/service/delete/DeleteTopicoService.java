package com.aluralatam.ForoAlura.domain.topico.service.delete;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.*;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
@RequiredArgsConstructor
@Service
public class DeleteTopicoService{
    private final TopicoRepository topicoRepository;
    @Validated
    @Transactional(rollbackFor = {
            ResourceNotFoundException.class,
            BusinessRuleException.class
    })
    public ResponseEntity<Response> deleteTopicFromDDBB(@Valid ConfirmDeleteTopic deleteTopic)
            throws ResourceNotFoundException, BusinessRuleException
    {
        final Long id=deleteTopic.id();
        final boolean confirm=deleteTopic.confirm();
        Topico topico=topicoRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException(Message.NO_ID_EXISTS)
        );
        if(!confirm)
            throw new BusinessRuleException(Message.NOT_CONFIRMED);
        topicoRepository.delete(topico);
        Response response=new Response(HttpStatus.ACCEPTED,Message.ELIMINATED);
        ResponseEntity body=ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        return body;
    }
}