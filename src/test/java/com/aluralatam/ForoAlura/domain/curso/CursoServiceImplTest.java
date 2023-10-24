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
    private Curso curso;
    private final Long id=1L;
    private final String nombre="Java";
    private final String categoria="Tratamiento de Excepciones";
    @BeforeEach
    void setUp(){
        curso=Curso.builder()
                .id(id)
                .nombre(nombre)
                .categoria(categoria)
                .inactivo(false)
                .build();
    }
    @AfterEach
    void tearDown() {
        curso=null;
    }
    // TEST SAVE METHOD
    @Test
    @DisplayName("Guarda & Retorna: ResponseEntity_Created")
    void itShouldReturnResponseEntity_StatusCreatedOnSave() throws EntityAlreadyExistsException {
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsByNombreAndCategoria(
                anyString(), anyString())).thenReturn(false);
        when(cursoRepository.save(any(Curso.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        ResponseEntity<Response> responseEntity=cursoService.save(dto);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.CREATED,responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository, times(1))
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("No_Guarda. Lanza EntityAlreadyExistsException")
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
    // TEST UPDATE METHOD
    @Test
    @DisplayName("Actualiza & Retorna: ResponseEntity_OK")
    void itShouldReturnResponseEntity_StatusOkOnUpdate() throws EntityAlreadyExistsException, ResourceNotFoundException {

        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString())).thenReturn(false);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(new Curso()));

        ResponseEntity<Response>responseEntity=cursoService.update(id,dto);

        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.UPDATED,responseEntity.getBody().getRespuesta());

        verify(cursoRepository,times(1)).existsById(id);
        verify(cursoRepository,times(1)).existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,times(1)).save(any(Curso.class));
    }
    @Test
    @DisplayName("No_Actualiza. Lanza ResourceNotFoundException")
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
    @DisplayName("No_Actualiza. Lanza: EntityAlreadyExistsException")
    void itShouldThrowEntityAlreadyExistsExceptionOnUpdate(){

        CUCursoDto dto = new CUCursoDto(nombre, categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString())).thenReturn(true);

        assertThrows(EntityAlreadyExistsException.class, () -> cursoService.update(id, dto));

        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository, times(1))
                .existsByNombreAndCategoria(dto.nombre(), dto.categoria());
        verify(cursoRepository, never()).findById(id);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    @DisplayName("Activa & Retorna: ResponseEntity_Accepted")
    void itShouldResponseEntity_StatusAcceptedOnActivate() throws AccountActivationException, ResourceNotFoundException {
        curso.setInactivo(true);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        ResponseEntity<Response>responseEntity=cursoService.activate(id);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("RECURSO REACTIVADO EXITOSAMENTE", responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository, times(1)).save(curso);
    }
    @Test
    @DisplayName("No_Activa. Lanza: AccountActivationException")
    void itShouldThrowAccountActivationExceptionOnActivate(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        assertThrows(AccountActivationException.class,
                ()->cursoService.activate(id)
        );
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("No_Activa. Lanza: ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnActivate(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.activate(id)
        );
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    // TEST DELETE METHOD
    @Test
    @DisplayName("Elimina & Retorna: ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteFromDDBB() throws BusinessRuleException, ResourceNotFoundException {
        boolean confirm=true;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);

        ResponseEntity<Response>responseEntity=cursoService.delete(deleteDto);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,times(1)).deleteById(id);
    }
    @Test
    @DisplayName("No_Elimina. Lanza: ResourceNotFoundException")
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
    @DisplayName("No_Elimina. Lanza BusinessRuleException")
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
    // TEST DISABLE METHOD
    @Test
    @DisplayName("Desactiva & Retorna: ResponseEntity_Accepted")
    void itShouldReturnResponseEntity_StatusAcceptedOnDisable() throws BusinessRuleException, ResourceNotFoundException, AccountActivationException {
        boolean inactivo=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,inactivo);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));

        ResponseEntity<Response>responseEntity=cursoService.disable(disableDto);

        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Message.ELIMINATED, responseEntity.getBody().getRespuesta());

        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,times(1)).save(curso);
    }
    @Test
    @DisplayName("No_Desactiva. Lanza ResourceNotFoundException")
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
    @DisplayName("No_Desactiva. Lanza NotConfirmedException")
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
    @DisplayName("No_Desactiva. Lanza AccountActivationException")
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
    // TEST GET-BY-ID METHOD
    @Test
    @DisplayName("Obtiene:ID & Retorna: ResponseEntity_FOUND")
    void itShouldReturnResponseEntity_StatusFoundOnGetById() throws ResourceNotFoundException {
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        ResponseEntity<Curso>responseEntity=cursoService.findById(id);

        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(curso, responseEntity.getBody());

        verify(cursoRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("No_Obtiene:ID. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findById(id)
        );
        verify(cursoRepository, times(1)).findById(id);
    }
    // TEST GET-BY-NOMBRE-AND-CATEGORIA METHOD
    @Test
    @DisplayName("Obtiene:NOMBRE & CATEGORIA. Retorna ResponseEntity_FOUND")
    void itShouldReturnResponseEntity_StatusFoundOnGetByNombreAndCategoria() throws ResourceNotFoundException{
        var getByNombre=this.nombre;
        var getByCategoria=this.categoria;
        when(cursoRepository.findByNombreAndCategoria
                (anyString(),anyString())).thenReturn(Optional.of(curso));
        ResponseEntity<Curso>responseEntity=cursoService.findByNombreAndCategoria(getByNombre, getByCategoria);

        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(curso, responseEntity.getBody());

        verify(cursoRepository, times(1)).findByNombreAndCategoria(getByNombre, getByCategoria);
    }
    @Test
    @DisplayName("No_Obtiene:NOMBRE & CATEGORIA. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnGetByNombreAndCategoria(){
        when(cursoRepository.findByNombreAndCategoria
                (anyString(),anyString())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findByNombreAndCategoria(this.nombre,this.categoria)
        );
        verify(cursoRepository, times(1))
                .findByNombreAndCategoria(this.nombre,this.categoria);
    }
    // TEST GET-ALL-BY-PAGINATION METHOD
    @Test
    @DisplayName("Encuentra cursos por paginacion")
    void itShouldReturnPaginationOfActiveCursos() throws EmptyEntityListException {
        var pageNumber = 1;
        var pageSize = 15;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        List<Curso> cursos = Arrays.asList(
                curso,
                Curso.builder().id(2L).nombre(this.nombre).categoria("JDBC").inactivo(true).build(),
                Curso.builder().id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true).build(),
                Curso.builder().id(4L).nombre(this.nombre).categoria("JUnit").inactivo(false).build()
        );
        Page<Curso> cursoPage = new PageImpl<>(cursos, pageRequest, cursos.size());
        when(cursoRepository.findAll(pageRequest)).thenReturn(cursoPage);

        ResponseEntity<List<Curso>>responseEntity=
                cursoService.findAllByPagination(pageNumber+"", pageSize+"");
        List<Curso> responseBody=responseEntity.getBody();

        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseBody);
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
                .filter(
                        curso-> curso.isInactivo()
                )
                .toList();
        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);

        verify(cursoRepository).findAll(pageRequest);
    }
    @Test
    @DisplayName("Lista_Vacía. Lanza EmptyEntityListException")
    void itShouldThrowEmptyEntityListExceptionOnFindAllCursosByPagination() {
        var pageNumber = 1;
        var pageSize = 15;
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        when(cursoRepository.findAll(pageRequest)).thenReturn(Page.empty());

        assertThrows(EmptyEntityListException.class,
                ()->cursoService.findAllByPagination(pageNumber+"", pageSize+"")
        );
        verify(cursoRepository).findAll(pageRequest);
    }
    @Test
    @DisplayName("Numero_Pagina o Elementos_Pagina menores <1. Lanza IllegalArgumentException")
    void itShouldReturnBadRequestWhenPageOrSizeLessThanOne(){
        var pageNumber = -1;
        var pageSize = 15;
        assertThrows(IllegalArgumentException.class,
                ()->cursoService.findAllByPagination(pageNumber+"",pageSize+"")
        );
        verify(cursoRepository, never()).findAll(any(Pageable.class));
    }
    @Test
    @DisplayName("Lanza: NumberFormatException")
    void itShouldReturnBadRequestOnNumberFormatExceptionOnFindALl(){
        var pageNumber = "asd";
        var pageSize = 15;
        assertThrows(NumberFormatException.class,
                ()->cursoService.findAllByPagination
                        (pageNumber, String.valueOf(pageSize))
        );
        verify(cursoRepository, never()).findAll(any(Pageable.class));
    }
    // TEST GET-ALL-BY-NOMBRE-AND-PAGINATION METHOD
    @Test
    @DisplayName("Encuentra cursos con nombre por paginación")
    void itShouldReturnPaginationOfCursosByNombre()throws ResourceNotFoundException {
        var pageNumber = "1";
        var pageSize = "15";
        var numPag=Integer.parseInt(pageNumber);
        var tamPag=Integer.parseInt(pageSize);

        PageRequest pageRequest = PageRequest.of(numPag - 1, tamPag);
        List<Curso> cursos = Arrays.asList(
                curso,
                Curso.builder().id(2L).nombre(nombre).categoria("JDBC").inactivo(true).build(),
                Curso.builder().id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true).build(),
                Curso.builder().id(4L).nombre(nombre).categoria("JUnit").inactivo(false).build()
        );
        Page<Curso> cursoPage = new PageImpl<>(cursos, pageRequest, cursos.size());
        when(cursoRepository.existsByNombre(this.nombre)).thenReturn(true);
        when(cursoRepository.search(this.nombre, pageRequest)).thenReturn(cursoPage);

        ResponseEntity<List<Curso>> responseEntity=
                cursoService.findAllByNombreAndPagination(this.nombre,pageNumber, pageSize);
        List<Curso>responseBody=responseEntity.getBody();

        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseBody);
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
                        curso->
                                curso.isInactivo()
                                        ||
                                !curso.getNombre().equals(nombre)
                )
                .toList();
        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);

        verify(cursoRepository, times(1)).existsByNombre(this.nombre);
        verify(cursoRepository).search(this.nombre,pageRequest);
    }
    @Test
    @DisplayName("No_Encuentra cursos con nombre. Lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionWhenSearchingCursosByNombre() {
        var pageNumber = "1";
        var pageSize = "15";
        var numPag=Integer.parseInt(pageNumber);
        var tamPag=Integer.parseInt(pageSize);
        Pageable pageable = PageRequest.of(numPag-1, tamPag);
        when(cursoRepository.existsByNombre(anyString())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> cursoService.findAllByNombreAndPagination
                        (this.nombre, pageNumber,pageSize)
        );
        verify(cursoRepository, times(1)).existsByNombre(this.nombre);
        verify(cursoRepository, never()).search(this.nombre,pageable);
    }
    @Test
    @DisplayName("Lanza un NumberFormatException")
    void itShouldReturnBadRequestWhenSizeIsLessThanOneWhenSearchingByNombre(){
        var pageNumber ="15";
        var pageSize ="asd";

        assertThrows(NumberFormatException.class, () -> {
            cursoService.findAllByNombreAndPagination(this.nombre,pageNumber, pageSize);
        });
        verify(cursoRepository, never()).existsByNombre(this.nombre);
        verify(cursoRepository, never()).search(anyString(),any(Pageable.class));
    }
    @Test
    @DisplayName("Lanza un NumberFormatException")
    void itShouldReturnBadRequestWhenPageIsLessThanOneWhenSearchingByNombre(){
        var pageNumber ="asd";
        var pageSize ="15";
        assertThrows(NumberFormatException.class, () -> {
            cursoService.findAllByNombreAndPagination(this.nombre,pageNumber, pageSize);
        });
        verify(cursoRepository, never()).existsByNombre(this.nombre);
        verify(cursoRepository, never()).search(anyString(),any(Pageable.class));
    }
    @Test
    @DisplayName("Numero_Pagina o Elementos_Pagina menores <1. Lanza IllegalArgumentException")
    void itShouldReturnBadRequestWhenPageOrSizeLessThanOneWhenFindByNombre(){
        var pageNumber = "-1";
        var pageSize = "15";

        assertThrows(IllegalArgumentException.class, () -> {
            cursoService.findAllByNombreAndPagination(this.nombre,pageNumber, pageSize);
        });
    }
}