package com.aluralatam.ForoAlura.domain.topico.service.create;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.CreateTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.create.CreateTopicoService;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CreateTopicoServiceTest{
    @Mock
    private TopicoRepository topicoRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CursoRepository cursoRepository;
    @InjectMocks
    private CreateTopicoService createTopicoService;
    private static final Long USUARIO_ID = 1L;
    private static final Long CURSO_ID = 2L;
    private static final CreateTopicDto DTO = new CreateTopicDto(
            "Titulo",
            "Mensaje de prueba",
            USUARIO_ID,
            CURSO_ID
    );
    @Test
    @DisplayName("(Crear)Retorna_ResponseEntity_Created")
    void itShouldReturnResponseEntity_StatusCreatedOnSave() throws ResourceNotFoundException {
        Usuario usuario = Usuario.builder().topicos(new ArrayList<>()).build();
        Curso curso = Curso.builder().topicos(new ArrayList<>()).build();
        when(usuarioRepository.findById(USUARIO_ID))
                .thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(CURSO_ID))
                .thenReturn(Optional.of(curso));
        when(topicoRepository.save(any(Topico.class)))
                .thenAnswer(invocation-> invocation.getArgument(0)
        );
        ResponseEntity<Topico>responseEntity =
                createTopicoService.createTopic(DTO);
        assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody()instanceof Topico);
        verify(usuarioRepository, times(1))
                .findById(USUARIO_ID);
        verify(cursoRepository, times(1))
                .findById(CURSO_ID);
        verify(topicoRepository, times(1))
                .save(any(Topico.class));
    }
    @Test
    @DisplayName("(Crear)Lanza_ResourceNotFoundException(USUARIO)")
    void itShouldThrowResourceNotFoundExceptionOnSaveByUser() {
        when(usuarioRepository.findById(USUARIO_ID))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()-> createTopicoService.createTopic(DTO));
        verify(usuarioRepository, times(1))
                .findById(USUARIO_ID);
        verify(cursoRepository, never())
                .findById(CURSO_ID);
        verify(topicoRepository, never())
                .save(any(Topico.class));
    }
    @Test
    @DisplayName("(Crear)Lanza_ResourceNotFoundException(CURSO)")
    void itShouldThrowResourceNotFoundExceptionOnSaveByCourse() {
        Usuario usuario = Usuario.builder().build();
        when(usuarioRepository.findById(USUARIO_ID))
                .thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(CURSO_ID))
                .thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()-> createTopicoService.createTopic(DTO));
        verify(usuarioRepository, times(1))
                .findById(USUARIO_ID);
        verify(cursoRepository, times(1))
                .findById(CURSO_ID);
        verify(topicoRepository, never())
                .save(any(Topico.class));
    }
}