package com.aluralatam.ForoAlura.domain.curso;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.CursoServiceImpl;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class CursoServiceImplTest {
    @Mock
    private CursoRepository cursoRepository;
    @InjectMocks
    private CursoServiceImpl cursoService;
    private final Curso curso=Curso.builder()
            .id(id)
            .nombre(nombre)
            .categoria(categoria)
            .inactivo(false)
            .build();
    private static final Long id=1L;
    private static final String nombre="Java";
    private static final String categoria="Tratamiento de Excepciones";
    private final List<Curso> cursos = new ArrayList<>();
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
    private PageRequest buildPageRequest() throws BusinessRuleException {
        return PageRequestConstructor.buildPageRequest(queryPageable);
    }
    private Page<Curso> buildPage() throws BusinessRuleException {
        cursos.add(curso);
        return new PageImpl<>(cursos,buildPageRequest(),cursos.size());
    }
    @AfterEach
    void tearDown() {
        cursos.clear();
    }
    @Test
    @DisplayName("(Crear)Retorna_ResponseEntity_Created")
    void itShouldReturnResponseEntity_StatusCreatedOnSave() throws EntityAlreadyExistsException {
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsByNombreAndCategoria(
                anyString(), anyString())).thenReturn(false);
        when(cursoRepository.save(any(Curso.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        ResponseEntity<Response> responseEntity=cursoService.save(dto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(Message.CREATED,responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository, times(1))
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("(Crear)Lanza_EntityAlreadyExistsException_")
    void itShouldThrowEntityAlreadyExistsExceptionOnSave(){
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsByNombreAndCategoria(
                anyString(),anyString())).thenReturn(true);
        assertThrows(EntityAlreadyExistsException.class,
                ()->cursoService.save(dto)
        );
        verify(cursoRepository,times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository,never())
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("(Actualizar)Retorna_ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOkOnUpdate() throws EntityAlreadyExistsException, ResourceNotFoundException {
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString())).thenReturn(false);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(new Curso()));
        ResponseEntity<Response>responseEntity=cursoService.update(id,dto);
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertEquals(Message.UPDATED,responseEntity.getBody().getRespuesta());

        verify(cursoRepository,times(1)).existsById(id);
        verify(cursoRepository,times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,times(1)).save(any(Curso.class));
    }
    @Test
    @DisplayName("(Actualizar)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnUpdate(){
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.update(id,dto)
        );
        verify(cursoRepository,times(1))
                .existsById(id);
        verify(cursoRepository,never())
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("(Actualizar)Lanza_EntityAlreadyExistsException")
    void itShouldThrowEntityAlreadyExistsExceptionOnUpdate(){
        CUCursoDto dto = new CUCursoDto(nombre, categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString()))
                .thenReturn(true);
        assertThrows(
                EntityAlreadyExistsException.class,
                () -> cursoService.update(id, dto)
        );
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository, times(1))
                .existsByNombreAndCategoria(dto.nombre(), dto.categoria());
        verify(cursoRepository, never()).findById(id);
        verify(cursoRepository, never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("(Activar)Retorna_ResponseEntity_Accepted")
    void itShouldResponseEntity_StatusAcceptedOnActivate() throws AccountActivationException, ResourceNotFoundException {
        curso.setInactivo(true);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        ResponseEntity<Response>responseEntity=cursoService.activate(id);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals("RECURSO REACTIVADO EXITOSAMENTE", responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository, times(1)).save(curso);
    }
    @Test
    @DisplayName("(Activar)Lanza_AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnActivate(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        assertThrows(AccountActivationException.class,
                ()->cursoService.activate(id)
        );
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("(Activar)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnActivate(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.activate(id)
        );
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("(Eliminar)Retorna_ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteFromDDBB() throws BusinessRuleException, ResourceNotFoundException {
        boolean confirm=true;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        ResponseEntity<Response>responseEntity=cursoService.delete(deleteDto);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,times(1)).deleteById(id);
    }
    @Test
    @DisplayName("(Eliminar)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDeleteFromDDBB(){
        boolean confirm=true;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.delete(deleteDto)
        );
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,never()).deleteById(id);
    }
    @Test
    @DisplayName("(Eliminar)Lanza_BusinessRuleException")
    void itShouldThrowNotConfirmedExceptionOnDeleteFromDDBB(){
        boolean confirm=false;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(BusinessRuleException.class,
                ()->cursoService.delete(deleteDto)
        );
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,never()).deleteById(id);
    }
    @Test
    @DisplayName("(Desactivar)Retorna_ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDisable() throws BusinessRuleException, ResourceNotFoundException, AccountActivationException {
        boolean inactivo=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,inactivo);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));

        ResponseEntity<Response>responseEntity=cursoService.disable(disableDto);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,times(1)).save(curso);
    }
    @Test
    @DisplayName("(Desactivar)Lanza_ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnDisable(){
        boolean confirm=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.disable(disableDto)
        );
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("(Desactivar)Lanza_BusinessRuleException")
    void itShouldThrowNotConfirmedExceptionOnDisable(){
        boolean confirm=false;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        assertThrows(BusinessRuleException.class,
                ()->cursoService.disable(disableDto)
        );
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(curso);
    }
    @Test
    @DisplayName("(Desactivar)Lanza__AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnDisable(){
        boolean confirm=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        curso.setInactivo(true);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        assertThrows(AccountActivationException.class,
                ()->cursoService.disable(disableDto)
        );
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(curso);
    }
    @Test
    @DisplayName("(Get)Retorna_ResponseEntity_FOUND(ID)")
    void itShouldReturnResponseEntity_StatusFoundOnGetById() throws ResourceNotFoundException {
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        ResponseEntity<Curso>responseEntity=cursoService.findById(id);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(curso, responseEntity.getBody());
        verify(cursoRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("(Get)Lanza_ResourceNotFoundException(ID)")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findById(id)
        );
        verify(cursoRepository, times(1)).findById(id);
    }
    // TEST GET-BY-NOMBRE-AND-CATEGORIA METHOD
    @Test
    @DisplayName("(Get)Retorna_ResponseEntity_FOUND(Nombre-Categoria)")
    void itShouldReturnResponseEntity_StatusFoundOnGetByNombreAndCategoria() throws ResourceNotFoundException, BusinessRuleException {
        var getByNombre=nombre;
        var getByCategoria=categoria;
        when(cursoRepository.findByNombreAndCategoria
                (anyString(),anyString())).thenReturn(Optional.of(curso));
        ResponseEntity<Curso>responseEntity=cursoService.findByNombreAndCategoria(
                getByNombre,
                getByCategoria
        );

        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals(curso, responseEntity.getBody());

        verify(cursoRepository, times(1)).findByNombreAndCategoria(getByNombre, getByCategoria);
    }
    @Test
    @DisplayName("(Get)Lanza_ResourceNotFoundException(Nombre-Categoria)")
    void itShouldThrowResourceNotFoundExceptionOnGetByNombreAndCategoria(){
        when(cursoRepository.findByNombreAndCategoria
                (anyString(),anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findByNombreAndCategoria(nombre,categoria)
        );
        verify(cursoRepository, times(1))
                .findByNombreAndCategoria(nombre,categoria);
    }
    @Test
    @DisplayName("(Get)Lanza_BusinessRuleException(Nombre:numerico)")
    void itShouldThrowBusinessRuleExceptionOnGetOneByNombre(){
        assertThrows(BusinessRuleException.class,
                ()->cursoService.findByNombreAndCategoria("456",categoria)
        );
        verify(cursoRepository, never())
                .findByNombreAndCategoria("456",categoria);
    }
    @Test
    @DisplayName("(Get)Lanza_BusinessRuleException(Categoria:vacio)")
    void itShouldThrowBusinessRuleExceptionOnGetOneByCategoria(){
        assertThrows(BusinessRuleException.class,
                ()->cursoService.findByNombreAndCategoria(nombre,"")
        );
        verify(cursoRepository, never())
                .findByNombreAndCategoria(nombre,"");
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Curso>")
    void itShouldReturnPaginationOfActiveCursos() throws EmptyEntityListException, BusinessRuleException {
        cursos.add(Curso.builder()
                .id(2L).nombre(nombre).categoria("JDBC").inactivo(true)
                .build());
        cursos.add(Curso.builder()
                .id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true)
                .build());
        cursos.add(Curso.builder()
                .id(4L).nombre(nombre).categoria("JUnit").inactivo(false)
                .build());
        Page<Curso> cursoPage = buildPage();
        when(cursoRepository.findAll(buildPageRequest())).thenReturn(cursoPage);
        ResponseEntity<List<Curso>>responseEntity=
                cursoService.findAllByPagination(queryPageable);
        List<Curso> responseBody=responseEntity.getBody();
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertFalse(responseBody.isEmpty());
        assertEquals(2, responseBody.size());
        //recorremos cada elemento de la pagina y cercioramos que inactivo==false
        responseBody.forEach(curso -> assertFalse(curso.isInactivo()));
        //lista de cursos filtrados
        List<Curso> filteredCourses = cursos.stream()
                .filter(Curso::isInactivo)
                .toList();
        //creamos una lista de cursos no filtrados con inactivo==true
        List<Curso> unfilteredCourses = cursos.stream()
                .filter(curso-> curso.isInactivo())
                .toList();
        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);
        verify(cursoRepository).findAll(buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_EmptyEntityListException:Lista<Curso>vacia")
    void itShouldThrowEmptyEntityListExceptionOnFindAllCursosByPagination() throws BusinessRuleException {
        when(cursoRepository.findAll(buildPageRequest())).thenReturn(Page.empty());
        assertThrows(EmptyEntityListException.class,
                ()->cursoService.findAllByPagination(queryPageable)
        );
        verify(cursoRepository).findAll(buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Curso>(NOMBRE)")
    void itShouldReturnPaginationOfCursosByNombre() throws ResourceNotFoundException, BusinessRuleException {
        cursos.add(Curso.builder().id(2L).nombre(nombre).categoria("JDBC").inactivo(true).build());
        cursos.add(Curso.builder().id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true).build());
        cursos.add(Curso.builder().id(4L).nombre(nombre).categoria("JUnit").inactivo(false).build());
        Page<Curso> cursoPage = buildPage();
        when(cursoRepository.existsByNombre(nombre)).thenReturn(true);
        when(cursoRepository.search(nombre, buildPageRequest())).thenReturn(cursoPage);
        ResponseEntity<List<Curso>> responseEntity=
                cursoService.findAllByNombreAndPagination(nombre,queryPageable);
        List<Curso>responseBody=responseEntity.getBody();
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertFalse(responseBody.isEmpty());
        assertEquals(2, responseBody.size());
        //recorremos cada elemento de la pagina y cercioramos que inactivo==false
        responseBody.forEach(curso -> assertFalse(curso.isInactivo()));
        //lista de cursos filtrados
        List<Curso> filteredCourses = cursos.stream()
                .filter(Curso::isInactivo)
                .toList();
        //creamos una lista de cursos no filtrados con nombre distinto o inactivo==true
        List<Curso> unfilteredCourses = cursos.stream()
                .filter(
                        curso->curso.isInactivo() ||
                        !curso.getNombre().equals(nombre)
                ).toList();
        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);
        verify(cursoRepository, times(1)).existsByNombre(nombre);
        verify(cursoRepository).search(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Curso>(NOMBRE))")
    void itShouldThrowResourceNotFoundExceptionWhenSearchingCursosByNombre()throws BusinessRuleException {
        when(cursoRepository.existsByNombre(anyString())).thenReturn(false);
        assertThrows(ResourceNotFoundException.class,
                () -> cursoService.findAllByNombreAndPagination
                        (nombre, queryPageable)
        );
        verify(cursoRepository, times(1)).existsByNombre(nombre);
        verify(cursoRepository, never()).search(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Curso>(NOMBRE:vacio))")
    void itShouldThrowBusinessRuleExceptionWhenSearchingCursosByNombre() throws BusinessRuleException {
        assertThrows(BusinessRuleException.class,
                () -> cursoService.findAllByNombreAndPagination
                        ("", queryPageable)
        );
        verify(cursoRepository, never()).search("",buildPageRequest());
    }
}