package com.aluralatam.ForoAlura.domain.topico.service.view;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RequiredArgsConstructor
@Service
public class ReadTopicoService{
    private final TopicoRepository topicoRepository;
    private final String errorMessage ="Error de validacion: ";
    @Transactional(readOnly = true)
    public ResponseEntity<Topico>getOneById(Long id) throws ResourceNotFoundException {
        Topico topico=topicoRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException(Message.NO_ID_EXISTS)
        );
        ResponseEntity<Topico>body=ResponseEntity.status(HttpStatus.OK).body(topico);
        return body;
    }
    @Transactional(readOnly = true)
    public ResponseEntity<List<Topico>> getAllByCourseName(
            String courseName,
            QueryPageable queryPageable
    )
            throws ResourceNotFoundException,BusinessRuleException
    {
        var isInvalidText = ChainChecker.invalidText(courseName);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        PageRequest pageRequest=PageRequestConstructor
                .buildPageRequest(queryPageable);
        Page<Topico> listaTopicos=topicoRepository.findAllByCourse(
                courseName,
                pageRequest
        );
        List<Topico> topicos=listaTopicos != null ? listaTopicos.getContent() : new ArrayList<>();
        if(topicos.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        ResponseEntity<List<Topico>>body=ResponseEntity.ok().body(topicos);
        return body;
    }
    @Transactional(readOnly = true)
    public ResponseEntity<List<Topico>> getAllByEmail(
            String email,
            QueryPageable queryPageable
    )
            throws ResourceNotFoundException,BusinessRuleException
    {
        var isInvalidEmail = ChainChecker.invalidEmail(email);
        if(isInvalidEmail != null)
            throw new BusinessRuleException(errorMessage+ isInvalidEmail);
        PageRequest pageRequest=PageRequestConstructor
                .buildPageRequest(queryPageable);
        Page<Topico> listaTopicos=topicoRepository.findAllByUser(
                email,
                pageRequest
        );
       List<Topico> topicos=listaTopicos != null ? listaTopicos.getContent() : new ArrayList<>();
        if(topicos.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        ResponseEntity<List<Topico>>body=ResponseEntity.ok().body(topicos);
        return body;
    }
    @Transactional(readOnly = true)
    public ResponseEntity<List<Topico>>getAllByTitle(
            String titulo,
            QueryPageable queryPageable
    )
            throws ResourceNotFoundException,BusinessRuleException
    {
        var isInvalidText = ChainChecker.invalidText(titulo);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        PageRequest pageRequest=PageRequestConstructor
                .buildPageRequest(queryPageable);
        Page<Topico> listaTopicos=topicoRepository.findAllByTitle(
                titulo,
                pageRequest
        );
        List<Topico>topicos=listaTopicos != null ? listaTopicos.getContent() : new ArrayList<>();
        if(topicos.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        ResponseEntity<List<Topico>>body=ResponseEntity.ok().body(topicos);
        return body;
    }
    @Transactional(readOnly = true)
    public ResponseEntity<List<Topico>>getAllByStatus(
            String status,
            QueryPageable queryPageable
    )
            throws ResourceNotFoundException,BusinessRuleException
    {
        var isInvalidText = ChainChecker.invalidText(status);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        var isStatus=ChainChecker.isStatus(status);
        if(!isStatus)
            throw new BusinessRuleException(
                    "No corresponde a ninguno de nuestros status."
            );
        StatusTopico statusTopico=ChainChecker.formattedStatus(status);
        PageRequest pageRequest=PageRequestConstructor
                .buildPageRequest(queryPageable);
        Page<Topico> listaTopicos=topicoRepository.findAllByStatus(
                statusTopico,
                pageRequest
        );
        List<Topico> topicos=listaTopicos != null ? listaTopicos.getContent() : new ArrayList<>();
        if(topicos.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        ResponseEntity<List<Topico>>body=ResponseEntity.ok().body(topicos);
        return body;
    }
    @Transactional(readOnly = true)
    public ResponseEntity<Topico>getOneByTitle(@Pattern(regexp="^[a-zA-Z ]+$") String titulo)
            throws ResourceNotFoundException,BusinessRuleException
    {
        var isInvalidText = ChainChecker.invalidText(titulo);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        Topico topico=topicoRepository.findByTitulo(titulo).orElseThrow(
                ()->new ResourceNotFoundException(Message.NO_PARAMETER_EXIST)
        );
        ResponseEntity<Topico>body=ResponseEntity.ok().body(topico);
        return body;
    }
}