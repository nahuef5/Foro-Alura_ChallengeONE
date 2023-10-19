package com.aluralatam.ForoAlura.domain.usuario.services.create;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CreateUsuarioServiceTest{
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private CreateUsuarioService createUsuarioService;
    @Test
    @DisplayName("Guarda & Retorna ResponseEntity_Created")
    void itShouldReturnResponseEntity_StatusCreatedOnSave(){
        CreateDatoPersonalDTO datoDTO=new CreateDatoPersonalDTO(
                "Rubby",
                "Gata",
                LocalDate.of(2000,3,23),
                "Argentina",
                "Cordoba",
                "Rio Ceballos"
        );
        CreateUsuarioDTO dto=new CreateUsuarioDTO(
                datoDTO,
                "rubbygata@email.com",
                "Abcdef_12345",
                "Abcdef_12345"
        );
        when(usuarioRepository
                .existsByEmail(anyString())).thenReturn(false);
        when(usuarioRepository
                .saveAndFlush(any(Usuario.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        ResponseEntity<Response> responseEntity=createUsuarioService
                .registerNewCommonUser(dto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.CREATED,responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(1))
                .existsByEmail(dto.email());
        verify(usuarioRepository, times(1))
                .saveAndFlush(any(Usuario.class));
    }
    @Test
    @DisplayName("No_Guarda. Lanza EntityAlreadyExistsException")
    void itShouldThrowEntityAlreadyExistsExceptionOnSave(){

        CreateDatoPersonalDTO datoDTO=new CreateDatoPersonalDTO(
                "Rubby",
                "Gata",
                LocalDate.of(2000,3,23),
                "Argentina",
                "Cordoba",
                "Rio Ceballos"
        );
        CreateUsuarioDTO dto=new CreateUsuarioDTO(
                datoDTO,
                "rubbygata@email.com",
                "Abcdef_12345",
                "Abcdef_12345"
        );
        when(usuarioRepository.existsByEmail(
                anyString())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class,
                ()->createUsuarioService.registerNewCommonUser(dto)
        );

        verify(usuarioRepository,times(1))
                .existsByEmail(dto.email());
        verify(usuarioRepository,never())
                .saveAndFlush(any(Usuario.class));
    }
    @Test
    @DisplayName("No_Guarda. Lanza EntityAlreadyExistsException")
    void itShouldThrowBusinessRuleExceptionOnSave(){
        CreateDatoPersonalDTO datoDTO=new CreateDatoPersonalDTO(
                "Rubby",
                "Gata",
                LocalDate.of(2000,3,23),
                "Argentina",
                "Cordoba",
                "Rio Ceballos"
        );
        CreateUsuarioDTO dto=new CreateUsuarioDTO(
                datoDTO,
                "rubbygata@email.com",
                "Abcdef_12345",
                "Acdef_2345"
        );
        assertThrows(BusinessRuleException.class,
                ()->createUsuarioService.registerNewCommonUser(dto)
        );
        verify(usuarioRepository,never())
                .existsByEmail(dto.email());
        verify(usuarioRepository,never())
                .saveAndFlush(any(Usuario.class));
    }
}