package com.aluralatam.ForoAlura.domain.topico.service.create;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.CreateTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
@AllArgsConstructor
@Service
@Transactional
public class CreateTopicoService{
    private final TopicoRepository topicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    @Validated
    @Transactional(rollbackFor =ResourceNotFoundException.class)
    public ResponseEntity<Topico>createTopic(@Valid CreateTopicDto dto)
            throws ResourceNotFoundException
    {
        var idUser= dto.usuario_id();
        var idCourse= dto.curso_id();
        var titulo=dto.titulo();
        var mensaje=dto.mensaje();
        var usuario=usuarioRepository.findById(idUser).orElseThrow(
                ()->new ResourceNotFoundException(Message.NO_ID_EXISTS+" (Usuario inexistente.)")
        );
        var curso=cursoRepository.findById(idCourse).orElseThrow(
                ()->new ResourceNotFoundException(Message.NO_ID_EXISTS+" (Curso inexistente.)")
        );
        Topico topico=new Topico(
                titulo,
                mensaje,
                usuario,
                curso
        );
        Topico created=topicoRepository.save(topico);
        usuario.addTopicoToList(created);
        curso.addTopicoToList(created);
        usuarioRepository.saveAndFlush(usuario);
        cursoRepository.saveAndFlush(curso);
        ResponseEntity<Topico> body=ResponseEntity.status(HttpStatus.CREATED).body(created);
        return body;
    }
}