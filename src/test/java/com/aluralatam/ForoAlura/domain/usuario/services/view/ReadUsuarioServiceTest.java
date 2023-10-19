package com.aluralatam.ForoAlura.domain.usuario.services.view;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ReadUsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ReadUsuarioService readUsuarioService;
    private Usuario usuario;
    private Long id=1L;
    List<Usuario> usuarios = new ArrayList<>();
    @BeforeEach
    void setUp() {
        DatoPersonal dato=DatoPersonal.builder()
                .nombre("Rubby")
                .apellido("Gata")
                .pais("Argentina")
                .build();
        usuario=Usuario.builder()
                .id(id)
                .dato(dato)
                .email("rubbygata@email.com")
                .build();
        usuarios.add(usuario);
    }
    @AfterEach
    void tearDown() {
        usuario=null;
        usuarios.clear();
    }
    @Test
    void testGetAllUsersByNameOrSurname_NumeroNegativo() {
        var pageNumber = "1";
        var pageSize = "-10";
        String[] sortingParams ={"dato.nombre","asc"};
        assertThrows(IllegalArgumentException.class,()->
                readUsuarioService.buildPageRequest(pageNumber,
                        pageSize,
                        sortingParams)
        );
    }
    @Test
    void testGetAllUsersByNameOrSurname_String() {
        var pageNumber = "dewed1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};
        assertThrows(IllegalArgumentException.class,()->
                readUsuarioService.buildPageRequest(pageNumber,
                        pageSize,
                        sortingParams)
        );
    }
    @Test
    @DisplayName("Obtiene:ID & Retorna: ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusFoundOnGetById(){
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity=readUsuarioService.getUserById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(usuario, responseEntity.getBody());

        verify(usuarioRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("No_Obtiene:ID. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->readUsuarioService.getUserById(id)
        );
        verify(usuarioRepository, times(1)).findById(anyLong());
    }
    @Test
    @DisplayName("Obtiene:Email & Retorna: ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOKOnGetByEmail(){
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity=readUsuarioService.getUserByEmail(anyString());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(usuario, responseEntity.getBody());

        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
    @Test
    @DisplayName("No_Obtiene:Email. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGetByEmail(){
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->readUsuarioService.getUserByEmail(anyString())
        );
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Paginacion_Nombre_Apellido & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfAllfUsersByNameOrSurname(){
        var nombreOApellido = "Rubby";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllUsersByNameOrSurname(nombreOApellido,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByNameOrSurname(
                                nombreOApellido,
                                pageNumber,
                                pageSize,
                                sortingParams
                        );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("NO_Paginacion_Nombre_Apellido & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUsersFoundByNameOrSurname(){
        var nombreOApellido = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
           readUsuarioService.getAllUsersByNameOrSurname(
                   nombreOApellido,
                   pageNumber,
                   pageSize,
                   sortingParams);
        });
        verify(usuarioRepository,never()).findAllUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("NO_Paginacion_Nombre_Apellido & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoUsersFoundByNameOrSurname() {
        var nombreOApellido ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllUsersByNameOrSurname(
                    nombreOApellido,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Activo)Paginacion_NombreApellido & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfActiveUsersByNameOrSurname(){
        var nombreOApellido = "Rubby";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllActiveUsersByNameOrSurname(nombreOApellido,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllActiveUsersByNameOrSurname(
                        nombreOApellido,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllActiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Activo)NO_Paginacion_Nombre_Apellido & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForActiveUsersFoundByNameOrSurname(){
        var nombreOApellido = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllActiveUsersByNameOrSurname(
                    nombreOApellido,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,never()).findAllActiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Activo)NO_Paginacion_Nombre_Apellido & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoActiveUsersFoundByNameOrSurname() {
        var nombreOApellido ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllActiveUsersByNameOrSurname(
                    nombreOApellido,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllActiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)Paginacion_NombreApellido & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfInactiveUsersByNameOrSurname(){
        var nombreOApellido = "Rubby";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllInactiveUsersByNameOrSurname(
                        nombreOApellido,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)NO_Paginacion_Nombre_Apellido & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForInactiveUsersFoundByNameOrSurname(){
        var nombreOApellido = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllInactiveUsersByNameOrSurname(
                    nombreOApellido,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)NO_Paginacion_Nombre_Apellido & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoInactiveUsersFoundByNameOrSurname() {
        var nombreOApellido ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllInactiveUsersByNameOrSurname(
                    nombreOApellido,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest);
    }
    @Test
    @DisplayName("Paginacion_Activo & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfUsersByActive(){
        var activo = true;
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.searchByActivo(activo,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByActivo(
                        activo,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).searchByActivo(activo,pageRequest);
    }
    @Test
    @DisplayName("NO_Paginacion_Activo & Lanza: EmptyEntityListException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUsersFoundByActive(){
        var activo = true;
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(EmptyEntityListException.class, () -> {
            readUsuarioService.getAllUsersByActivo(
                    activo,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1))
                .searchByActivo(activo,pageRequest);
    }
    @Test
    @DisplayName("(ALL)Paginacion_Pais & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfUsersByCountry(){
        var pais = "Argentina";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllByCountry(pais,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByCountry(
                        pais,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(ALL)NO_Paginacion_Pais & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUsersFoundByCountry(){
        var pais = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,never()).findAllByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(ALL)NO_Paginacion_Pais & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoUsersFoundByCountry() {
        var pais ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Activo)Paginacion_Pais & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfActiveUsersByCountry(){
        var pais = "Argentina";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllActiveUsersByCountry(pais,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllActiveUsersByCountry(
                        pais,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllActiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Activo)NO_Paginacion_Pais & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForActiveUsersFoundByCountry(){
        var pais = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllActiveUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,never()).findAllActiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Activo)NO_Paginacion_Pais & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoActiveUsersFoundByCountry() {
        var pais ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllActiveUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllActiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)Paginacion_Pais & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfInactiveUsersByCountry(){
        var pais = "Argentina";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllInactiveUsersByCountry(pais,pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllInactiveUsersByCountry(
                        pais,
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllInactiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)NO_Paginacion_Pais & Lanza: BusinessRuleException")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForInactiveUsersFoundByCountry(){
        var pais = "";
        var pageNumber ="1";
        var pageSize ="10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);

        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(Inactivo)NO_Paginacion_Pais & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoInactiveUsersFoundByCountry() {
        var pais ="inexistente";
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.pais","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllInactiveUsersByCountry(pais,pageRequest);
    }
    @Test
    @DisplayName("(ALL-ACTIVOS)Paginacion & Retorna: ResponseEntity_OK")
    void itShouldReturnPaginationOfAllfUsers(){
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};

        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        Page<Usuario>page= new PageImpl<>(usuarios,pageRequest,usuarios.size());
        when(usuarioRepository.findAllAssets(pageRequest)).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersToCommonUsers(
                        pageNumber,
                        pageSize,
                        sortingParams
                );
        assertNotNull(responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllAssets(pageRequest);
    }
    @Test
    @DisplayName("(ALL-ACTIVOS)NO_Paginacion_Activo & Lanza: ResourceNotFoundException")
    void itShouldThrowAResourceNotFoundExceptionWhenNoUsersFound() {
        var pageNumber = "1";
        var pageSize = "10";
        String[] sortingParams ={"dato.nombre","asc"};
        PageRequest pageRequest =readUsuarioService.buildPageRequest(
                pageNumber,
                pageSize,
                sortingParams);
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllUsersToCommonUsers(
                    pageNumber,
                    pageSize,
                    sortingParams);
        });
        verify(usuarioRepository,times(1)).findAllAssets(pageRequest);
    }
}