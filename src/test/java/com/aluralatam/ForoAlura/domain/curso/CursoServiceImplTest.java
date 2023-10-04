package com.aluralatam.ForoAlura.domain.curso;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.CursoServiceImpl;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.Response;
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
    @DisplayName("Guarda y retorna un ResponseEntity con status Created con un mensaje")
    void itShouldReturnResponseEntity_StatusCreatedOnSave(){
        //Arrange
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsByNombreAndCategoria(
                anyString(), anyString())).thenReturn(false);
        when(cursoRepository.save(any(Curso.class))).thenAnswer(
                invocation -> invocation.getArgument(0)
        );
        //Act
        ResponseEntity<Response> responseEntity=cursoService.save(dto);
        //Asserts
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Response.CREATED,responseEntity.getBody().getRespuesta());
        //Verifications
        verify(cursoRepository, times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository, times(1))
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("No guarda y lanza EntityAlreadyExistsException si ya existe nombre y categoria")
    void itShouldThrowEntityAlreadyExistsExceptionOnSave(){
        //Arrange
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsByNombreAndCategoria(
                anyString(),anyString())).thenReturn(true);
        //Act and assert
        assertThrows(EntityAlreadyExistsException.class,
                ()->cursoService.save(dto)
        );
        //Verifications
        verify(cursoRepository,times(1))
                .existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository,never())
                .save(any(Curso.class));
    }

    // TEST UPDATE METHOD

    @Test
    @DisplayName("Actualiza y retorna un ResponseEntity con status OK con un mensaje")
    void itShouldReturnResponseEntity_StatusOkOnUpdate() throws ResourceNotFoundException, EntityAlreadyExistsException{
        //Arrange
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString())).thenReturn(false);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(new Curso()));
        //Act
        ResponseEntity<Response>responseEntity=cursoService.update(id,dto);
        //Asserts
        assertEquals(HttpStatus.OK,responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Response.UPDATED,responseEntity.getBody().getRespuesta());
        //Verificatios
        verify(cursoRepository,times(1)).existsById(id);
        verify(cursoRepository,times(1)).existsByNombreAndCategoria(dto.nombre(),dto.categoria());
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,times(1)).save(any(Curso.class));
    }
    @Test
    @DisplayName("No actualiza y lanza ResourceNotFoundException si no existe un curso con ese id")
    void itShouldThrowResourceNotFoundExceptionOnUpdate(){
        //Arrange
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(false);
        //Act and assert
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.update(id,dto)
        );
        //Verifications
        verify(cursoRepository,times(1))
                .existsById(id);
        verify(cursoRepository,never())
                .save(any(Curso.class));
    }
    @Test
    @DisplayName("No actualiza y lanza EntityAlreadyExistsException si ya existe nombre y categoria")
    void itShouldThrowEntityAlreadyExistsExceptionOnUpdate() {
        //Arrange
        CUCursoDto dto = new CUCursoDto(nombre, categoria);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        when(cursoRepository.existsByNombreAndCategoria(anyString(), anyString())).thenReturn(true);
        //Act and assert
        assertThrows(EntityAlreadyExistsException.class, () -> cursoService.update(id, dto));
        //Verifications
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository, times(1)).existsByNombreAndCategoria(dto.nombre(), dto.categoria());
        verify(cursoRepository, never()).findById(id);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    // TEST ACTIVE METHOD

    @Test
    @DisplayName("Activa y retorna un ResponseEntity con status Accepted con un mensaje")
    void itShouldResponseEntity_StatusAcceptedOnActivate() throws ResourceNotFoundException, AccountActivationException{
        //Arrange
        curso.setInactivo(true);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);
        //Act
        ResponseEntity<Response>responseEntity=cursoService.activate(id);
        //Asserts
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals("RECURSO REACTIVADO EXITOSAMENTE", responseEntity.getBody().getRespuesta());
        assertFalse(curso.isInactivo());
        //Verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository, times(1)).save(curso);
    }
    @Test
    @DisplayName("No activa y lanza AccountActivationException si ya se encuentra activo")
    void itShouldThrowAccountActivationExceptionOnActivate(){
        //Arrange
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        //Act and assert
        assertThrows(AccountActivationException.class,
                ()->cursoService.activate(id)
        );
        //Verifications
        verify(cursoRepository,times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("No activa y lanza ResourceNotFoundException si no existe curso con ese id")
    void itShouldThrowResourceNotFoundExceptionOnActivate(){
        //Arrange
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        //Act and assert
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.activate(id)
        );
        //Verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }

    // TEST DELETE METHOD

    @Test
    @DisplayName("Elimina de ddbb y retorna ResponseEntity con Status Accepted con mensaje")
    void itShouldReturnResponseEntity_StatusAcceptedOnDeleteFromDDBB()throws ResourceNotFoundException, NotConfirmedException{
        //Arrange
        boolean confirm=true;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        //Act
        ResponseEntity<Response>responseEntity=cursoService.delete(deleteDto);
        //Asserts
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Response.ELIMINATED, responseEntity.getBody().getRespuesta());
        //Verifications
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,times(1)).deleteById(id);
    }
    @Test
    @DisplayName("No elimina de ddbb y lanza ResourceNotFoundException por id inexistente")
    void itShouldThrowResourceNotFoundExceptionOnDeleteFromDDBB(){
        //Arrange
        boolean confirm=true;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(false);
        //Act and assert
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.delete(deleteDto)
        );
        //Verifications
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,never()).deleteById(id);
    }
    @Test
    @DisplayName("No elimina de ddbb y lanza NotConfirmedException por no confirmar")
    void itShouldThrowNotConfirmedExceptionOnDeleteFromDDBB(){
        //Arrange
        boolean confirm=false;
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.existsById(anyLong())).thenReturn(true);
        //Act and assert
        assertThrows(NotConfirmedException.class,
                ()->cursoService.delete(deleteDto)
        );
        //Verifications
        verify(cursoRepository, times(1)).existsById(id);
        verify(cursoRepository,never()).deleteById(id);
    }

    // TEST DISABLE METHOD

    @Test
    @DisplayName("Desactiva y retorna ResponseEntity con Status Accepted con mensaje")
    void itShouldReturnResponseEntity_StatusAcceptedOnDisable()throws ResourceNotFoundException, NotConfirmedException{
        //Arrange
        boolean confirm=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        //Act
        ResponseEntity<Response>responseEntity=cursoService.disable(disableDto);
        //Asserts
        assertTrue(curso.isInactivo());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(Response.ELIMINATED, responseEntity.getBody().getRespuesta());
        //Verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,times(1)).save(curso);
    }
    @Test
    @DisplayName("No desactiva y lanza ResourceNotFoundException por id inexistente")
    void itShouldThrowResourceNotFoundExceptionOnDisable(){
        //Arrange
        boolean confirm=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        //Act and assert
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.disable(disableDto)
        );
        //verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(any(Curso.class));
    }
    @Test
    @DisplayName("No desactiva y lanza NotConfirmedException por no confirmar")
    void itShouldThrowNotConfirmedExceptionOnDisable(){
        //arrange
        boolean confirm=false;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        //act and asserts
        assertThrows(NotConfirmedException.class,
                ()->cursoService.disable(disableDto)
        );
        assertFalse(curso.isInactivo());
        //verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(curso);
    }
    @Test
    @DisplayName("Lanza AccountActivationException por estar inactivo")
    void itShouldThrowAccountActivationExceptionOnDisable(){
        //arrange
        boolean confirm=true;
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,confirm);
        curso.setInactivo(true);
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        //act and asserts
        assertThrows(AccountActivationException.class,
                ()->cursoService.disable(disableDto)
        );
        assertTrue(curso.isInactivo());
        //verifications
        verify(cursoRepository, times(1)).findById(id);
        verify(cursoRepository,never()).save(curso);
    }

    // TEST GET-BY-ID METHOD

    @Test
    @DisplayName("Obtiene por id y retorna un ResponseEntity Status FOUND con el curso")
    void itShouldReturnResponseEntity_StatusFoundOnGetById() throws ResourceNotFoundException{
        //arrange
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.of(curso));
        //act
        ResponseEntity<Curso>responseEntity=cursoService.findById(id);
        //asserts
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(curso, responseEntity.getBody());
        //verification
        verify(cursoRepository, times(1)).findById(id);
    }
    @Test
    @DisplayName("No obtiene y lanza un ResourceNotFoundException cuando busca por id")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        //arrange
        when(cursoRepository.findById(anyLong())).thenReturn(Optional.empty());
        //act and aseert
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findById(id)
        );
        //verification
        verify(cursoRepository, times(1)).findById(id);
    }

    // TEST GET-BY-NOMBRE-AND-CATEGORIA METHOD

    @Test
    @DisplayName("Obtiene por nombre y categoria y retorna un ResponseEntity Status FOUND con el curso")
    void itShouldReturnResponseEntity_StatusFoundOnGetByNombreAndCategoria() throws ResourceNotFoundException{
        //arrange
        var getByNombre=this.nombre;
        var getByCategoria=this.categoria;
        when(cursoRepository.findByNombreAndCategoria(anyString(),anyString())).thenReturn(Optional.of(curso));
        //act
        ResponseEntity<Curso>responseEntity=cursoService.findByNombreAndCategoria(getByNombre, getByCategoria);
        //asserts
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(curso, responseEntity.getBody());
        //verification
        verify(cursoRepository, times(1)).findByNombreAndCategoria(getByNombre, getByCategoria);
    }
    @Test
    @DisplayName("No obtiene y lanza un ResourceNotFoundException cuando busca por nombre y categoria")
    void itShouldThrowResourceNotFoundExceptionOnGetByNombreAndCategoria(){
        //arrange
        var getByNombre=this.nombre;
        var getByCategoria=this.categoria;
        when(cursoRepository.findByNombreAndCategoria(anyString(),anyString())).thenReturn(Optional.empty());
        //act and asserts
        assertThrows(ResourceNotFoundException.class,
                ()->cursoService.findByNombreAndCategoria(getByNombre,getByCategoria)
        );
        //verification
        verify(cursoRepository, times(1)).findByNombreAndCategoria(getByNombre,getByCategoria);
    }

    // TEST GET-ALL-BY-NOMBRE-AND-PAGINATION METHOD

    @Test
    @DisplayName("Encuentra cursos por nombre y paginacion")
    public void itShouldReturnPaginationOfAllCursosByNombre() throws ResourceNotFoundException, EmptyEntityListException {
        //ARRANGE
        Pageable pageable = Pageable.unpaged();
        //se crea un lista de cursos
        curso.setInactivo(true);
        List<Curso> cursos = Arrays.asList(
                curso,
                Curso.builder().id(2L).nombre(nombre).categoria("P-O-O").inactivo(true).build(),
                Curso.builder().id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true).build(),
                Curso.builder().id(4L).nombre(nombre).categoria("JDBC").inactivo(false).build()
        );
        //generamos pagina de cursos
        Page<Curso> cursoPage = new PageImpl<>(cursos, pageable, cursos.size());

        when(cursoRepository.existsByNombre(nombre)).thenReturn(true);
        when(cursoRepository.search(nombre, pageable)).thenReturn(cursoPage);
        //ACT
        ResponseEntity<Page<Curso>> responseEntity = cursoService
                .findAllByNombreAndPagination(nombre, pageable);

        Page<Curso>responseBody = responseEntity.getBody();
        //ASSERTS
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertEquals(1, responseBody.getTotalElements());

        //recorremos cada elemento de la pagina y cercioramos que inactivo==false
        responseBody.forEach(curso->assertFalse(curso.isInactivo()));
        //lista de los cursos filtrados
        List<Curso> filteredCourses = responseEntity.getBody().getContent();
        //creamos una lista de cursos no filtrados con nombre distinto o inactivo==true
        List<Curso> unfilteredCourses = cursos.stream()
                .filter(
                        curso->
                                curso.isInactivo()==true
                                        ||
                                        !curso.getNombre().equals(nombre)
                )
                .toList();
        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);
        System.out.println("Paginacion por nombre");
        System.out.println("\n Q CURSOS FILTRADOS: "+filteredCourses.size());
        System.out.println("\n Q CURSOS NO FILTRADOS: "+expected);
        System.out.println("\n DIFERENCIA ENTRE LISTA CURSO PRINCIPAL Y Q CURSOS FILTRADOS: "+actual);

        //VERIFICATIONS
        verify(cursoRepository, times(1)).existsByNombre(nombre);
        verify(cursoRepository).search(nombre,pageable);
    }
    @Test
    @DisplayName("No encuentra cursos por ese nombrey lanza ResourceNotFoundException")
    void itShouldThrowResourceNotFoundExceptionOnFindAllCursosByNombreAndPagination() {
        //arrange
        var getByombre = this.nombre;
        Pageable pageable = PageRequest.of(0, 10);
        when(cursoRepository.existsByNombre(anyString())).thenReturn(false);
        //act and assert
        assertThrows(ResourceNotFoundException.class,
                () -> cursoService.findAllByNombreAndPagination(getByombre, pageable)
        );
        //verifications
        verify(cursoRepository, times(1)).existsByNombre(getByombre);
        verify(cursoRepository, never()).search(getByombre,pageable);
    }
    @Test
    @DisplayName("Lista vacia cursos con ese nombre, lanza EmptyEntityListException")
    void itShouldThrowEmptyEntityListExceptionOnFindAllCursosByNombreAndPagination() {
        //arrange
        Pageable pageable = PageRequest.of(0, 10);
        when(cursoRepository.existsByNombre(anyString())).thenReturn(true);
        when(cursoRepository.search(anyString(), any(Pageable.class))).thenReturn(Page.empty());
        //act and assert
        assertThrows(EmptyEntityListException.class,
                () -> cursoService.findAllByNombreAndPagination(nombre, pageable)
        );
        //verifications
        verify(cursoRepository).search(eq(nombre),eq(pageable));
    }

    // TEST GET-ALL-BY-NOMBRE-AND-PAGINATION METHOD

    @Test
    @DisplayName("Encuentra cursos por paginacion")
    public void itShouldReturnPaginationOfAllCursos() throws EmptyEntityListException {
        //arrange
        Pageable pageable = Pageable.unpaged();
        List<Curso> cursos = Arrays.asList(
                curso,
                Curso.builder().id(2L).nombre(nombre).categoria("JDBC").inactivo(true).build(),
                Curso.builder().id(3L).nombre("Ruby").categoria("P-O-O").inactivo(true).build(),
                Curso.builder().id(4L).nombre(nombre).categoria("JUnit").inactivo(false).build()
        );
        Page<Curso> cursoPage = new PageImpl<>(cursos, pageable, cursos.size());
        when(cursoRepository.findAll(pageable)).thenReturn(cursoPage);
        //act
        ResponseEntity<Page<Curso>> responseEntity = cursoService
                .findAllByPagination(pageable);
        Page<Curso>responseBody = responseEntity.getBody();
        //asserts
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertEquals(2, responseBody.getTotalElements());

        //recorremos cada elemento de la pagina y cercioramos que inactivo==false
        responseBody.forEach(curso->assertFalse(curso.isInactivo()));
        //lista de los cursos filtrados
        List<Curso> filteredCourses = responseEntity.getBody().getContent();
        //creamos una lista de cursos no filtrados con inactivo==true
        List<Curso> unfilteredCourses = cursos.stream()
                .filter(
                        curso->curso.isInactivo()==true
                )
                .toList();

        var expected=unfilteredCourses.size();
        var actual=cursos.size() - filteredCourses.size();
        assertEquals(expected, actual);
        System.out.println("Solo paginacion");
        System.out.println("\n Q CURSOS FILTRADOS: "+filteredCourses.size());
        System.out.println("\n Q CURSOS NO FILTRADOS: "+expected);
        System.out.println("\n DIFERENCIA ENTRE LISTA CURSO PRINCIPAL Y Q CURSOS FILTRADOS: "+actual);
        //verifications
        verify(cursoRepository).findAll(pageable);
    }
    @Test
    @DisplayName("Lista vacia cursos, lanza EmptyEntityListException")
    void itShouldThrowEmptyEntityListExceptionOnFindAllCursosByPagination() {
        //arrange
        Pageable pageable = Pageable.unpaged();
        when(cursoRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());
        //act and assert
        assertThrows(EmptyEntityListException.class,
                () -> cursoService.findAllByPagination(pageable)
        );
        //verification
        verify(cursoRepository).findAll(eq(pageable));
    }
}