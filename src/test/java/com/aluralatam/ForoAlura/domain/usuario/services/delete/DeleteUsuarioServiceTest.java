package com.aluralatam.ForoAlura.domain.usuario.services.delete;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class DeleteUsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private DeleteUsuarioService deleteUsuarioService;
    private Usuario usuario,user,use,us,u;
    private Long id=1L,id2=2L,id3=3L,id4=4L,id5=5L;

    @BeforeEach
    void setUp() {
        DatoPersonal dato=DatoPersonal.builder()
                .nombre("Rubby")
                .apellido("Gata")
                .fechaNacimiento(LocalDate.of(2000, 3, 23))
                .pais("Argentina")
                .provincia("Cordoba")
                .localidad("Rio Ceballos")
                .build();
        usuario=Usuario.builder()
                .id(id)
                .dato(dato)
                .email("rubbygata@email.com")
                .contrasena("Abcdef_12345")
                .activo(true)
                .build();
        user=Usuario.builder()
                .id(id2)
                .dato(dato)
                .email("rubbygata2@email.com")
                .contrasena("Abcdef_12345")
                .activo(true)
                .build();
        use=Usuario.builder()
                .id(id3)
                .dato(dato)
                .email("rubbygata3@email.com")
                .contrasena("Abcdef_12345")
                .activo(true)
                .build();
        us=Usuario.builder()
                .id(id4)
                .dato(dato)
                .email("rubbygata4@email.com")
                .contrasena("Abcdef_12345")
                .activo(false)
                .build();
        u=Usuario.builder()
                .id(id5)
                .dato(dato)
                .email("rubbygata5@email.com")
                .contrasena("Abcdef_12345")
                .activo(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        usuario=null;
        user=null;
        use=null;
        us=null;
        u=null;
    }

    @Test
    @DisplayName("Elimina & Retorna ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteFromDDBB()throws ResourceNotFoundException{
        usuario.setActivo(false);
        RemoveUsuarioDto dto=new RemoveUsuarioDto(id,true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Response> responseEntity=deleteUsuarioService.deleteUserFromDDBB(dto);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository,times(1)).delete(usuario);
    }
    @Test
    @DisplayName("No_Elimina. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteFromDDBB(){
        RemoveUsuarioDto deleteDto=new RemoveUsuarioDto(id,true);
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->deleteUsuarioService.deleteUserFromDDBB(deleteDto)
        );
        verify(usuarioRepository, times(1)).findById(anyLong());
        verify(usuarioRepository,never()).delete(usuario);
    }
    @Test
    @DisplayName("No_Elimina. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnDeleteFromDDBB(){
        RemoveUsuarioDto deleteDto=new RemoveUsuarioDto(id,true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        assertThrows(AccountActivationException.class,
                ()->deleteUsuarioService.deleteUserFromDDBB(deleteDto)
        );
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository,never()).delete(usuario);
    }
    @Test
    @DisplayName("No_Elimina. Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnDeleteFromDDBB(){
        usuario.setActivo(false);
        RemoveUsuarioDto deleteDto=new RemoveUsuarioDto(id,false);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        assertThrows(BusinessRuleException.class,
                ()->deleteUsuarioService.deleteUserFromDDBB(deleteDto)
        );
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository,never()).delete(usuario);
    }

    @Test
    @DisplayName("Elimina_Lista. Retorna ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id4,id5);
        List<Usuario>users=Arrays.asList(us,u);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(users);
        doNothing().when(usuarioRepository).deleteAllByIdInBatch(ids);

        ResponseEntity<Response> responseEntity=deleteUsuarioService.deleteUsersFromDDBB(dto);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(2)).findAllById(ids);
        verify(usuarioRepository,times(1)).deleteAllByIdInBatch(ids);
    }
    @Test
    @DisplayName("No_Elimina_Lista. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(8L,9L);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(anyList());

        assertThrows(ResourceNotFoundException.class,
                ()->deleteUsuarioService.deleteUsersFromDDBB(dto)
        );
        verify(usuarioRepository, times(2)).findAllById(ids);
        verify(usuarioRepository,never()).deleteAllByIdInBatch(ids);
    }
    @Test
    @DisplayName("No_Elimina_Lista. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id,id2);
        List<Usuario>users=Arrays.asList(usuario,user);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(users);
        assertThrows(AccountActivationException.class,
                ()->deleteUsuarioService.deleteUsersFromDDBB(dto)
        );
        verify(usuarioRepository, times(3)).findAllById(ids);
        verify(usuarioRepository,never()).deleteAllByIdInBatch(ids);
    }
    @Test
    @DisplayName("No_Elimina_Lista. Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id4,id5);
        List<Usuario>users=Arrays.asList(us,u);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,false);
        when(usuarioRepository.findAllById(ids)).thenReturn(users);

        assertThrows(BusinessRuleException.class,
                ()->deleteUsuarioService.deleteUsersFromDDBB(dto)
        );
        verify(usuarioRepository, times(1)).findAllById(ids);
        verify(usuarioRepository,never()).deleteAllByIdInBatch(ids);
    }
}