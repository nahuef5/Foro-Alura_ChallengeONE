package com.aluralatam.ForoAlura.domain.usuario.services.modifications;
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
public class ModifyUsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ModifyUsuarioService modifyUsuarioService;
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
    @DisplayName("Actualiza Info Personal. Retorna ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOkOnUpdate(){
        UpdateDatoPersonalDTO dto=new UpdateDatoPersonalDTO(
                "Rubby Verde",
                "Gata Peque",
                "Córdoba",
                "Río Ceballos"
        );
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Response> responseEntity=
                modifyUsuarioService.updateUserByPersonalInformation(id, dto);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.UPDATED,responseEntity.getBody().getRespuesta());

        verify(usuarioRepository,times(1)).findById(id);
        verify(usuarioRepository,times(1)).saveAndFlush(usuario);
    }
    @Test
    @DisplayName("No_Actualiza Info Personal. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnUpdate(){
        UpdateDatoPersonalDTO dto=new UpdateDatoPersonalDTO(
                "Rubby Verde",
                "Gata Peque",
                "Córdoba",
                "Río Ceballos"
        );
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->modifyUsuarioService.updateUserByPersonalInformation(id,dto)
        );
        verify(usuarioRepository,times(1)).findById(id);
        verify(usuarioRepository,never()).saveAndFlush(any(Usuario.class));
    }
    @Test
    @DisplayName("Actualiza_Password. Retorna ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOkOnGenerateANewPassword(){
        var email="rubbygata@email.com";
        NuevaContrasena dto=new NuevaContrasena(
                email,
                "Xxxx_123456",
                "Xxxx_123456"
        );
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        ResponseEntity<Response> responseEntity=
                modifyUsuarioService.generateANewPassword(dto);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.UPDATED,responseEntity.getBody().getRespuesta());

        verify(usuarioRepository,times(1)).findByEmail(email);
        verify(usuarioRepository,times(1)).saveAndFlush(usuario);
    }
    @Test
    @DisplayName("No_Actualiza_Password. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGenerateANewPassword(){
        var email="inexistente@email.com";
        NuevaContrasena dto=new NuevaContrasena(
                email,
                "Xxxx_123456",
                "Xxxx_123456"
        );
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->modifyUsuarioService.generateANewPassword(dto)
        );
        verify(usuarioRepository,times(1)).findByEmail(email);
        verify(usuarioRepository,never()).saveAndFlush(any(Usuario.class));
    }
    @Test
    @DisplayName("No_Actualiza_Password. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnGenerateANewPassword(){
        usuario.setActivo(false);
        var email="rubbygata@email.com";
        NuevaContrasena dto=new NuevaContrasena(
                email,
                "Xxxx_123456",
                "Xxxx_123456"
        );
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        assertThrows(AccountActivationException.class,
                ()->modifyUsuarioService.generateANewPassword(dto)
        );
        verify(usuarioRepository,times(1)).findByEmail(email);
        verify(usuarioRepository,never()).saveAndFlush(usuario);
    }
    @Test
    @DisplayName("No_Actualiza_Password. Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnGenerateANewPassword(){
        var email="rubbygata@email.com";
        NuevaContrasena dto=new NuevaContrasena(
                email,
                "Abcde_123456",
                "Xxxxx_123456"
        );
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        assertThrows(BusinessRuleException.class,
                ()->modifyUsuarioService.generateANewPassword(dto)
        );
        verify(usuarioRepository,times(1)).findByEmail(email);
        verify(usuarioRepository,never()).saveAndFlush(usuario);
    }
    @Test
    @DisplayName("Desactiva & Retorna ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDisable(){
        RemoveUsuarioDto dto=new RemoveUsuarioDto(id,true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).setInactiveToUser(id);
        ResponseEntity<Response> responseEntity=modifyUsuarioService.disableAccount(dto);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository,times(1)).setInactiveToUser(id);
    }
    @Test
    @DisplayName("No_Desactiva. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteFromDDBB(){
        Long _id=9L;
        RemoveUsuarioDto dto=new RemoveUsuarioDto(_id,true);
        when(usuarioRepository.findById(_id)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->modifyUsuarioService.disableAccount(dto)
        );
        verify(usuarioRepository, times(1)).findById(anyLong());
        verify(usuarioRepository,never()).setInactiveToUser(_id);
    }
    @Test
    @DisplayName("No_Desactiva. Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnDeleteFromDDBB(){
        Long _id=9L;
        RemoveUsuarioDto dto=new RemoveUsuarioDto(_id,false);
        when(usuarioRepository.findById(_id)).thenReturn(Optional.of(usuario));

        assertThrows(BusinessRuleException.class,
                ()->modifyUsuarioService.disableAccount(dto)
        );
        verify(usuarioRepository, times(1)).findById(_id);
        verify(usuarioRepository,never()).setInactiveToUser(_id);
    }
    @Test
    @DisplayName("No_Desactiva. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnDeleteFromDDBB(){
        usuario.setActivo(false);
        RemoveUsuarioDto dto=new RemoveUsuarioDto(id,true);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));

        assertThrows(AccountActivationException.class,
                ()->modifyUsuarioService.disableAccount(dto)
        );
        verify(usuarioRepository, times(1)).findById(id);
        verify(usuarioRepository,never()).setInactiveToUser(id);
    }
    @Test
    @DisplayName("Desactiva_Lista. Retorna ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id,id2);
        List<Usuario>users=Arrays.asList(usuario,user);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(users);
        doNothing().when(usuarioRepository).setInactiveToUserList(users);

        ResponseEntity<Response> responseEntity=modifyUsuarioService.disableAccounts(dto);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(3)).findAllById(ids);
        verify(usuarioRepository,times(1)).setInactiveToUserList(users);
    }
    @Test
    @DisplayName("No_Desactiva_Lista. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(8L,9L);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(anyList());

        assertThrows(ResourceNotFoundException.class,
                ()->modifyUsuarioService.disableAccounts(dto)
        );
        verify(usuarioRepository, times(2)).findAllById(ids);
        verify(usuarioRepository,never()).setInactiveToUserList(anyList());
    }
    @Test
    @DisplayName("Desactiva_Lista. Lanza BusinessRuleException")
    void itShouldThrowBusinessRuleExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id,id2);
        List<Usuario>users=Arrays.asList(usuario,user);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,false);
        when(usuarioRepository.findAllById(ids)).thenReturn(users);

        assertThrows(BusinessRuleException.class,
                ()->modifyUsuarioService.disableAccounts(dto)
        );
        verify(usuarioRepository, times(1)).findAllById(ids);
        verify(usuarioRepository,never()).setInactiveToUserList(users);
    }
    @Test
    @DisplayName("No_Desactiva_Lista. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnDeleteUsersFromDDBB(){
        List<Long>ids=Arrays.asList(id4,id5);
        List<Usuario>users=Arrays.asList(us,u);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,true);

        when(usuarioRepository.findAllById(ids)).thenReturn(users);

        assertThrows(AccountActivationException.class,
                ()->modifyUsuarioService.disableAccounts(dto)
        );
        verify(usuarioRepository, times(3)).findAllById(ids);
        verify(usuarioRepository,never()).setInactiveToUserList(users);
    }
    @Test
    @DisplayName("Reactiva & Retorna ResponseEntity_Accepted")
    void itShouldResponseEntity_StatusAcceptedOnReactivate(){
        var email="rubbygata@email.com";
        usuario.setActivo(false);
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenReturn(usuario);

        ResponseEntity<Response>responseEntity=modifyUsuarioService
                .reactivateAccountByEmail(email);

        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(Message.REACTIVATED_RESOURCE, responseEntity.getBody().getRespuesta());

        verify(usuarioRepository, times(1))
                .findByEmail(email);
        verify(usuarioRepository, times(1))
                .saveAndFlush(usuario);
    }
    @Test
    @DisplayName("No_Reactiva. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnActivate(){

        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                ()->modifyUsuarioService.reactivateAccountByEmail(anyString())
        );

        verify(usuarioRepository, times(1)).findByEmail(anyString());
        verify(usuarioRepository,never()).saveAndFlush(any(Usuario.class));
    }
    @Test
    @DisplayName("No_Reactiva. Lanza AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnActivate(){
        //Arrange
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        //Act and assert
        assertThrows(AccountActivationException.class,
                ()->modifyUsuarioService.reactivateAccountByEmail(anyString())
        );
        //Verifications
        verify(usuarioRepository,times(1)).findByEmail(anyString());
        verify(usuarioRepository,never()).saveAndFlush(any(Usuario.class));
    }
}