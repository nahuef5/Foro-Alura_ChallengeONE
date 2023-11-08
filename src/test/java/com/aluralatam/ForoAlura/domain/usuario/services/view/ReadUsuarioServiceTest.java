package com.aluralatam.ForoAlura.domain.usuario.services.view;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
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
public class ReadUsuarioServiceTest{
    @Mock
    private UsuarioRepository usuarioRepository;
    @InjectMocks
    private ReadUsuarioService readUsuarioService;
    private Usuario usuario;
    private Long id=1L;
    private List<Usuario> usuarios = new ArrayList<>();
    @BeforeEach
    void setUp() {
        DatoPersonal dato=DatoPersonal.builder()
                .nombre("Rubby")
                .apellido("Test")
                .pais("Argentina")
                .build();
        usuario=Usuario.builder()
                .id(id)
                .dato(dato)
                .email("rubbytest@email.com")
                .build();
        usuarios.add(usuario);
    }
    @AfterEach
    void tearDown() {
        usuario=null;
        usuarios.clear();
    }
    private final QueryPageable queryPageable=new QueryPageable(){
        private final Integer page=1,elementByPage=10;
        private final String[] sortingParams=new String[]{"curso.nombre", "asc"};
        @Override
        public Integer getPage() {
            return page;
        }
        @Override
        public Integer getElementByPage(){
            return elementByPage;
        }
        @Override
        public String[] sortingParams(){
            return sortingParams;
        }};
    private PageRequest buildPageRequest()throws BusinessRuleException {
        return PageRequestConstructor.buildPageRequest(queryPageable);
    }
    private Page<Usuario> buildPage() throws BusinessRuleException{
        return new PageImpl<>(usuarios,buildPageRequest(),usuarios.size());
    }
    @Test
    @DisplayName("(GET)Retorna_ResponseEntity_OK(ID)")
    void itShouldReturnResponseEntityWithAUserById()
            throws ResourceNotFoundException
    {
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity=readUsuarioService.getUserById(id);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(usuario, responseEntity.getBody());

        verify(usuarioRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("(Get)Lanza_ResourceNotFoundException(ID)")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->readUsuarioService.getUserById(id)
        );
        verify(usuarioRepository, times(1)).findById(anyLong());
    }
    @Test
    @DisplayName("(GET)Retorna_ResponseEntity_OK(EMAIL)")
    void itShouldToReturnResponseEntityWithAUserByEmail()
            throws ResourceNotFoundException
    {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity=readUsuarioService.getUserByEmail(anyString());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(usuario, responseEntity.getBody());
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
    @Test
    @DisplayName("(GET)Lanza_ResourceNotFoundException(EMAIL)")
    void itShouldThrowResourceNotFoundExceptionOnGetByEmail(){
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->readUsuarioService.getUserByEmail(anyString())
        );
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(Nombre-Apellido)")
    void itShouldToReturnListUserByNameOrSurname()
            throws BusinessRuleException, ResourceNotFoundException
    {
        var nombreOApellido = "Rubby";

        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllUsersByNameOrSurname(nombreOApellido,buildPageRequest())).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByNameOrSurname(
                                nombreOApellido,
                                queryPageable
                        );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Usuario>(Nombre:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUsersFoundByNameOrSurname()
            throws BusinessRuleException
    {
        var nombreOApellido = "";
        assertThrows(BusinessRuleException.class, () -> {
           readUsuarioService.getAllUsersByNameOrSurname(
                   nombreOApellido,
                   queryPageable
                   );
        });
        verify(usuarioRepository,never()).findAllUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Usuario>(Nombre)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoUsersFoundByNameOrSurname()
            throws BusinessRuleException
    {
        var nombreOApellido ="inexistente";
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllUsersByNameOrSurname(
                    nombreOApellido,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1)).findAllUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Activo)Retorna_ResponseEntity_Body:Lista<Usuario>(Nombre-Apellido)")
    void itShouldToReturnListOfActiveUsersByNameOrSurname()
            throws BusinessRuleException, ResourceNotFoundException
    {
        var nombreOApellido = "Rubby";

        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllActiveUsersByNameOrSurname(nombreOApellido,buildPageRequest())).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllActiveUsersByNameOrSurname(
                        nombreOApellido,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllActiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Activo)Lanza_BusinessRuleException:Lista<Usuario>(Nombre:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForActiveUsersFoundByNameOrSurname()
            throws BusinessRuleException
    {
        var nombreOApellido = "";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllActiveUsersByNameOrSurname(
                    nombreOApellido,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllActiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Activo)Lanza_ResourceNotFoundExceptionException:Lista<Usuario>(Nombre)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoActiveUsersFoundByNameOrSurname()
            throws BusinessRuleException
    {
        var nombreOApellido ="inexistente";
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllActiveUsersByNameOrSurname(
                    nombreOApellido,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1)).findAllActiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Inactivo)Retorna_ResponseEntity_Body:Lista<Usuario>(Nombre-Apellido)")
    void itShouldToReturnListOfInactiveUsersByNameOrSurname()
            throws BusinessRuleException, ResourceNotFoundException
    {
        var nombreOApellido = "Rubby";
        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllInactiveUsersByNameOrSurname(nombreOApellido,buildPageRequest())).thenReturn(page);
        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllInactiveUsersByNameOrSurname(
                        nombreOApellido,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllInactiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Inactivo)Lanza_BusinessRuleException:Lista<Usuario>(Nombre)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForInactiveUsersFoundByNameOrSurname()
            throws BusinessRuleException{
        var nombreOApellido = "";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllInactiveUsersByNameOrSurname(
                    nombreOApellido,
                    queryPageable
            );
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("(Inactivo)Lanza_ResourceNotFoundExceptionException:Lista<Usuario>(Nombre)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoInactiveUsersFoundByNameOrSurname()
            throws BusinessRuleException{
        var nombreOApellido ="inexistente";
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllInactiveUsersByNameOrSurname(
                    nombreOApellido,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1)).findAllInactiveUsersByNameOrSurname(nombreOApellido,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(Activo==true)")
    void itShouldToReturnListUserByActive()
            throws EmptyEntityListException,BusinessRuleException
    {
        var activo = true;
        Page<Usuario>page= buildPage();
        when(usuarioRepository.searchByActivo(activo,buildPageRequest())).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByActivo(
                        activo,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).searchByActivo(activo,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_EmptyEntityListException:Lista<Usuario>(Activo)")
    void itShouldThrowAEmptyEntityListExceptionWhenSearchingForUsersFoundByActive()
            throws BusinessRuleException{
        var activo = true;
        assertThrows(EmptyEntityListException.class, () -> {
            readUsuarioService.getAllUsersByActivo(
                    activo,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1))
                .searchByActivo(activo,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(PAIS)")
    void itShouldToReturnResponseEntityWithUserListByCountry()
            throws BusinessRuleException, ResourceNotFoundException
    {
        var pais = "Argentina";
        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllByCountry(pais,buildPageRequest())).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllUsersByCountry(
                        pais,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Usuario>(PAIS:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUsersFoundByCountry()
            throws BusinessRuleException{
        var pais = "";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_IllegalArgumentException:Lista<Usuario>(PAIS:No-Enum)")
    void itShouldThrowIllegalArgumentExceptionWhenSearchingForUsersFoundByCountry()
            throws BusinessRuleException{
        var pais = "xascasfe";
        assertThrows(IllegalArgumentException.class, () -> {
            readUsuarioService.getAllUsersByCountry(
                    pais,
                    queryPageable
                );
        });
        verify(usuarioRepository,never()).findAllByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Usuario>(PAIS)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoUsersFoundByCountry()
            throws BusinessRuleException{
        var pais ="Bolivia";

        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1)).findAllByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(ACTIVO)Lista<Usuario>(PAIS)")
    void itShouldToReturnListOfActiveUsersByCountry()
            throws BusinessRuleException, ResourceNotFoundException
    {
        var pais = "Argentina";
        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllActiveUsersByCountry(pais,buildPageRequest())).thenReturn(page);
        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllActiveUsersByCountry(
                        pais,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllActiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:(ACTIVO)Lista<Usuario>(PAIS:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForActiveUsersFoundByCountry()
            throws BusinessRuleException
    {
        var pais = "";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllActiveUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllActiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_IllegalArgumentException:(ACTIVO)Lista<Usuario>(PAIS:No-Enum)")
    void itShouldThrowIllegalArgumentExceptionWhenSearchingForActiveUsersFoundByCountry()
            throws BusinessRuleException{
        var pais = "xascasfe";
        assertThrows(IllegalArgumentException.class, () -> {
            readUsuarioService.getAllActiveUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllActiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:(ACTIVO)Lista<Usuario>(PAIS)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoActiveUsersFoundByCountry()
            throws BusinessRuleException
    {
        var pais ="Bolivia";
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllActiveUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,times(1)).findAllActiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(INACTIVO)Lista<Usuario>(PAIS)")
    void itShouldToReturnListOfInactiveUsersByCountry()
            throws BusinessRuleException, ResourceNotFoundException{
        var pais = "Argentina";

        Page<Usuario>page= buildPage();
        when(usuarioRepository.findAllInactiveUsersByCountry(pais,buildPageRequest())).thenReturn(page);

        ResponseEntity<List<Usuario>> responseEntity=
                readUsuarioService.getAllInactiveUsersByCountry(
                        pais,
                        queryPageable
                );
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertFalse(responseEntity.getBody().isEmpty());
        verify(usuarioRepository,times(1)).findAllInactiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:(INACTIVO)Lista<Usuario>(PAIS:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForInactiveUsersFoundByCountry()
            throws BusinessRuleException{
        var pais = "";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_IllegalArgumentException:(INACTIVO)Lista<Usuario>(PAIS:No-Enum)")
    void itShouldThrowIllegalArgumentExceptionWhenSearchingForInactiveUsersFoundByCountry()
            throws BusinessRuleException{
        var pais = "xascasfe";
        assertThrows(IllegalArgumentException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    queryPageable
                    );
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:(INACTIVO)Lista<Usuario>(PAIS)")
    void itShouldThrowAResourceNotFoundExceptionWhenNoInactiveUsersFoundByCountry()
            throws BusinessRuleException{
        var pais ="Bolivia";
        assertThrows(ResourceNotFoundException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    queryPageable
            );
        });
        verify(usuarioRepository,times(1)).findAllInactiveUsersByCountry(pais,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:(INACTIVO)Lista<Usuario>(PAIS:numero)")
    void itShouldThrowBusinessRuleExceptionWhenNoInactiveUsersFoundByCountry()
            throws BusinessRuleException{
        var pais ="466";
        assertThrows(BusinessRuleException.class, () -> {
            readUsuarioService.getAllInactiveUsersByCountry(
                    pais,
                    queryPageable
            );
        });
        verify(usuarioRepository,never()).findAllInactiveUsersByCountry(pais,buildPageRequest());
    }
}