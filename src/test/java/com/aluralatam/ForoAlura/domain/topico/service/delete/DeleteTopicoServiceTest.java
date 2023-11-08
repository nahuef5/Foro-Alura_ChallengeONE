package com.aluralatam.ForoAlura.domain.topico.service.delete;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.ConfirmDeleteTopic;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DeleteTopicoServiceTest{
    @Mock
    private TopicoRepository topicoRepository;
    @InjectMocks
    private DeleteTopicoService deleteTopicoService;
    private Topico topico=Topico.builder()
            .id(ID)
            .build();
    private static final Long ID =1L;

    @Test
    @DisplayName("(Eliminar)Retorna_ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteFromDDBB()
    throws ResourceNotFoundException, BusinessRuleException{
        ConfirmDeleteTopic dto=new ConfirmDeleteTopic(ID,true);
        when(topicoRepository.findById(ID)).thenReturn(Optional.of(topico));
        ResponseEntity<Response> responseEntity=deleteTopicoService.deleteTopicFromDDBB(dto);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());
        verify(topicoRepository, times(1)).findById(ID);
        verify(topicoRepository,times(1)).delete(topico);
    }

    @Test
    @DisplayName("(Eliminar)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteFromDDBB(){
        ConfirmDeleteTopic delete=new ConfirmDeleteTopic(ID,true);
        when(topicoRepository.findById(ID)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->deleteTopicoService.deleteTopicFromDDBB(delete)
        );
        verify(topicoRepository, times(1)).findById(ID);
        verify(topicoRepository,never()).delete(topico);
    }
    @Test
    @DisplayName("(Eliminar)Lanza_BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnDeleteFromDDBB(){
        ConfirmDeleteTopic delete=new ConfirmDeleteTopic(ID,false);
        when(topicoRepository.findById(ID)).thenReturn(Optional.of(topico));
        assertThrows(BusinessRuleException.class,
                ()->deleteTopicoService.deleteTopicFromDDBB(delete)
        );
        verify(topicoRepository, times(1)).findById(ID);
        verify(topicoRepository,never()).delete(topico);
    }
}
