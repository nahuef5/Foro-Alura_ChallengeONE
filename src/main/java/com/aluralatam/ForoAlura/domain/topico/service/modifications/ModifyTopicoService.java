package com.aluralatam.ForoAlura.domain.topico.service.modifications;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.*;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.ChainChecker;
import com.aluralatam.ForoAlura.global.tools.Message;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
@RequiredArgsConstructor
@Service
public class ModifyTopicoService{
    private final TopicoRepository topicoRepository;
    @Validated
    @Transactional(rollbackFor = ResourceNotFoundException.class)
    public ResponseEntity<Topico> editTopic(
            @Min(value = 0,message = "Coloque un id numerico valido.")Long id,
            @Valid ModifyTopicDto dto)
            throws ResourceNotFoundException
    {
        final String titulo=dto.titulo();
        final String mensaje=dto.mensaje();
        Topico topico=topicoRepository.findById(id)
                .orElseThrow(
            ()->new ResourceNotFoundException(Message.NO_ID_EXISTS+
                    " (Topico Inexistente)")
        );
        topico.setTitulo(titulo);
        topico.setMensaje(mensaje);
        Topico updated=topicoRepository.save(topico);
        ResponseEntity<Topico>body=ResponseEntity
                .status(HttpStatus.ACCEPTED).body(updated);
        return body;
    }
    @Transactional(rollbackFor ={
            ResourceNotFoundException.class,
            BusinessRuleException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Topico> changeTopicStatus(
            Long id,
            String statusTopico
    )
            throws BusinessRuleException, ResourceNotFoundException
    {
        if(statusTopico.isEmpty())
            throw new BusinessRuleException("No debe estar vacio el status");
        if(!ChainChecker.isStatus(statusTopico))
            throw new BusinessRuleException("No corresponde a ninguno de nuestros status.");
        Topico topico=topicoRepository.findById(id)
                .orElseThrow(
            ()->new ResourceNotFoundException(Message.NO_ID_EXISTS+ " (Topico Inexistente)")
        );
        var isEmptyRespuestaList=topico.getRespuestas().isEmpty();
        if(isEmptyRespuestaList)
            throw new BusinessRuleException(
                    "Debe existir una Respuesta como mínimo para cambiar el status."
            );
        var formattedStatus=ChainChecker.formattedStatus(statusTopico);

        topico.setStatus(formattedStatus);
        Topico updated=topicoRepository.save(topico);
        ResponseEntity<Topico>body=ResponseEntity.status(HttpStatus.ACCEPTED).body(updated);
        return body;
    }
}