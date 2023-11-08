package com.aluralatam.ForoAlura.domain.topico.service.modifications;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.respuesta.model.entity.Respuesta;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.ModifyTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ModifyTopicoServiceTest{
    @Mock
    private TopicoRepository topicoRepository;
    @InjectMocks
    private ModifyTopicoService modifyTopicoService;
    private static final Long ID=1L;
    private Topico topico=Topico.builder()
            .id(ID)
            .titulo("Titulo")
            .mensaje("Mensaje")
            .status(StatusTopico.NO_RESPONDIDO)
            .fechaCreacion(LocalDateTime.now())
            .autor(Usuario.builder().build())
            .curso(Curso.builder().build())
            .respuestas(new ArrayList<>())
            .build();
    @Test
    @DisplayName("(Editar_Topico)Retorna ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOkOnEditTopic()
            throws ResourceNotFoundException
    {
        var title="New Title";
        var message="Message Test";
        ModifyTopicDto dto=new ModifyTopicDto(title,message);
        when(topicoRepository.findById(ID)).thenReturn(Optional.of(topico));
        when(topicoRepository.save(topico))
                .thenReturn(topico
        );
        ResponseEntity<Topico> responseEntity=
                modifyTopicoService.editTopic(ID,dto);
        var actualStatus=responseEntity.getStatusCode();
        var actualBody=responseEntity.getBody();
        assertEquals(HttpStatus.ACCEPTED, actualStatus);

        assertEquals(title, actualBody.getTitulo());
        assertEquals(message, actualBody.getMensaje());

        verify(topicoRepository,times(1))
                .findById(ID);
        verify(topicoRepository,times(1))
                .save(topico);
    }
    @Test
    @DisplayName("(Editar_Topico)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGenerateANewPassword(){
        var title="New Title";
        var message="Message Test";
        ModifyTopicDto dto=new ModifyTopicDto(title,message);

        when(topicoRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->modifyTopicoService.editTopic(ID,dto)
        );
        verify(topicoRepository,times(1)).findById(ID);
        verify(topicoRepository,never()).saveAndFlush(any(Topico.class));
    }

    @Test
    @DisplayName("(Cambiar_StatusTopico)Retorna_ResponseEntity_Accepted")
    void itShouldResponseEntity_StatusAcceptedOnChangeStatus()
            throws BusinessRuleException, ResourceNotFoundException
    {
        topico.addRespuestaToList(Respuesta.builder().build());
        var status="Solucionado";
        when(topicoRepository.findById(ID)).thenReturn(Optional.of(topico));
        when(topicoRepository.save(topico)).thenReturn(topico);

        ResponseEntity<Topico>responseEntity=modifyTopicoService
                .changeTopicStatus(ID,status);

        var actualStatus=responseEntity.getStatusCode();
        var expectedBody=StatusTopico.SOLUCIONADO;
        var actualBody=responseEntity.getBody().getStatus();
        assertEquals(HttpStatus.ACCEPTED, actualStatus);
        assertEquals(expectedBody, actualBody);

        verify(topicoRepository, times(1))
                .findById(ID);
        verify(topicoRepository, times(1))
                .save(topico);
    }
    @Test
    @DisplayName("(Cambiar_StatusTopico)Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnChangeStatus()
    {
        when(topicoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->modifyTopicoService.changeTopicStatus(ID,"Solucionado")
        );
        verify(topicoRepository, times(1))
                .findById(anyLong());
        verify(topicoRepository, never()).save(topico);
    }
    @Test
    @DisplayName("(Cambiar_StatusTopico:Sin-Respuestas)Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionForEmptyResponses()
    {
        when(topicoRepository.findById(ID)).thenReturn(Optional.of(topico));
        assertThrows(BusinessRuleException.class,
                ()->modifyTopicoService.changeTopicStatus(ID,"Solucionado")
        );
        verify(topicoRepository, times(1))
                .findById(anyLong());
        verify(topicoRepository, never()).save(topico);
    }
    @Test
    @DisplayName("(Cambiar_StatusTopico:String-vacio)Lanza_BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionForEmptyString()
    {
        var status="";
        assertThrows(BusinessRuleException.class,
                ()->modifyTopicoService.changeTopicStatus(ID,status)
        );
        verify(topicoRepository, never())
                .findById(anyLong());
        verify(topicoRepository, never()).save(topico);
    }
    @Test
    @DisplayName("(Cambiar_StatusTopico:Bad-String)Lanza_BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionForBadString()
    {
        var status="test";
        assertThrows(BusinessRuleException.class,
                ()->modifyTopicoService.changeTopicStatus(ID,status)
        );
        verify(topicoRepository, never())
                .findById(anyLong());
        verify(topicoRepository, never()).save(topico);
    }
}