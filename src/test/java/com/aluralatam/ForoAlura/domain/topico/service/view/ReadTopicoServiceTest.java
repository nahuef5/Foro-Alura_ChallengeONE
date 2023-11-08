package com.aluralatam.ForoAlura.domain.topico.service.view;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.PageRequestConstructor;
import com.aluralatam.ForoAlura.global.tools.QueryPageable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class ReadTopicoServiceTest{
    @Mock
    private TopicoRepository topicoRepository;
    @InjectMocks
    private ReadTopicoService readTopicoService;
    private static final Long ID=1L;
    private final Usuario USUARIO=Usuario.builder()
            .id(ID)
            .dato(
                DatoPersonal.builder()
                        .nombre("TestName")
                        .apellido("TestLastName")
                        .build()
            )
            .email("testname@email.com")
            .build();
    private final Curso CURSO=Curso.builder()
            .id(ID)
            .nombre("Java Orientado a Objetos")
            .build();

    private final Topico topico=Topico.builder()
            .id(ID)
            .titulo("Titulo")
            .mensaje("Mensaje")
            .status(StatusTopico.NO_RESPONDIDO)
            .curso(CURSO)
            .autor(USUARIO)
            .respuestas(new ArrayList<>())
            .build();
    private final List<Topico> topicos = new ArrayList<>();
    private StatusTopico formattedStatus(String status){
        var statusTopico = status.toUpperCase().replace(" ", "_");
        return StatusTopico.valueOf(statusTopico);
    }
    private QueryPageable queryPageable=new QueryPageable(){
        private Integer page=1,elementByPage=10;
        private String[] sortingParams=new String[]{"curso.nombre", "asc"};
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
        PageRequest pageRequest = PageRequestConstructor.buildPageRequest(queryPageable);
        return pageRequest;
    }
    private Page<Topico> buildPage() throws BusinessRuleException {
        topicos.add(topico);
        return new PageImpl<>(topicos,buildPageRequest(),topicos.size());
    }
    @Test
    @DisplayName("(GET)Retorna_ResponseEntity_OK(ID)")
    void itShouldBeAbleToGetATopicById() throws ResourceNotFoundException {
        when(topicoRepository.findById(ID))
                .thenReturn(Optional.of(topico));
        ResponseEntity<Topico>responseEntity=readTopicoService.getOneById(ID);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Topico.class,responseEntity.getBody());

        verify(topicoRepository, times(1)).findById(ID);
    }

    @Test
    @DisplayName("(GET)Lanza_ResourceNotFoundException(ID)")
    void itShouldThrowResourceNotFoundExceptionOnGetById(){
        when(topicoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                ()->readTopicoService.getOneById(ID)
        );
        verify(topicoRepository, times(1)).findById(anyLong());
    }
    @Test
    @DisplayName("(GET)Retorna_ResponseEntity_OK(TITULO)")
    void itShouldBeAbleToGetATopicByTitle() throws ResourceNotFoundException, BusinessRuleException {
        var titulo="Titulo";
        when(topicoRepository.findByTitulo(titulo))
                .thenReturn(Optional.of(topico));
        ResponseEntity<Topico>responseEntity=readTopicoService.getOneByTitle(titulo);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertInstanceOf(Topico.class,responseEntity.getBody());

        verify(topicoRepository, times(1)).findByTitulo(titulo);
    }

    @Test
    @DisplayName("(GET)Lanza_ResourceNotFoundException(TITULO)")
    void itShouldThrowResourceNotFoundExceptionOnGetByTitle(){
        when(topicoRepository.findByTitulo(anyString())).thenReturn(Optional.empty());
        assertThrows(
                ResourceNotFoundException.class,
                ()->readTopicoService.getOneByTitle("Test")
        );
        verify(topicoRepository, times(1)).findByTitulo(anyString());
    }
    @Test
    @DisplayName("No_Obtiene x Titulo. Lanza BusinessRuleException")
    void itShouldThrowResourceNotFoundExceptionOnGetByEmptyTitle(){
        assertThrows(
                BusinessRuleException.class,
                ()->readTopicoService.getOneByTitle("")
        );
        verify(topicoRepository, never()).findByTitulo(anyString());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(NombreCurso)")
    void itShouldBeAbleToGetATopicListByCourseName() throws ResourceNotFoundException, BusinessRuleException {
        var nombre="Java Orientado a Objetos";
        Page<Topico>page= buildPage();
        when(topicoRepository.findAllByCourse(nombre,buildPageRequest()))
                .thenReturn(page);
        ResponseEntity<List<Topico>>responseEntity=
                readTopicoService.getAllByCourseName(
                    nombre,
                    queryPageable
                );
        var topicoActual=responseEntity.getBody().get(0);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(topico, topicoActual);

        verify(topicoRepository, times(1)).findAllByCourse(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(NombreCurso:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForCourseFoundByName() throws BusinessRuleException {
        var nombre = "";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByCourseName(
                 nombre, queryPageable
             ));
        verify(topicoRepository,never()).findAllByCourse(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(NombreCurso:numerico)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForCourseFoundByNameWithNumbers() throws BusinessRuleException {
        var nombre = "1235651";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByCourseName(
                        nombre, queryPageable
                ));
        verify(topicoRepository,never()).findAllByCourse(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Topico>(NombreCurso)")
    void itShouldThrowAResourceNotFoundExceptionWhenSearchingForCourseFoundByName() throws BusinessRuleException {
        var nombre = "ascasc";
        assertThrows(
                ResourceNotFoundException.class,
                ()-> readTopicoService.getAllByCourseName(
                      nombre,queryPageable
            ));
        verify(topicoRepository,times(1)).findAllByCourse(nombre,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(EMAIL)")
    void itShouldBeAbleToGetATopicByEmail() throws ResourceNotFoundException, BusinessRuleException {
        var email="testname@email.com";
        Page<Topico>page= buildPage();
        when(topicoRepository.findAllByUser(email,buildPageRequest()))
                .thenReturn(page);
        ResponseEntity<List<Topico>>responseEntity=
                readTopicoService.getAllByEmail(
                    email,queryPageable
                );
        var topicoActual=responseEntity.getBody().get(0);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(topico, topicoActual);

        verify(topicoRepository, times(1)).findAllByUser(email,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(EMAIL:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUserFoundByName() throws BusinessRuleException {
        var email = "";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByEmail(
                 email,queryPageable
             ));
        verify(topicoRepository,never()).findAllByUser(email,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(EMAIL:numerico)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForUserFoundByNameWithNumbers() throws BusinessRuleException {
        var email = "12345";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByEmail(
                        email,queryPageable
                ));
        verify(topicoRepository,never()).findAllByUser(email,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Topico>(EMAIL)")
    void itShouldThrowAResourceNotFoundExceptionWhenSearchingForUserFoundByName() throws BusinessRuleException {
        var email = "ascasc@asd.asd";
        assertThrows(
                ResourceNotFoundException.class,
                ()-> readTopicoService.getAllByEmail(
                email,queryPageable
        ));
        verify(topicoRepository,times(1)).findAllByUser(email,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(TITULO)")
    void itShouldBeAbleToGetAllTopicByTitle() throws ResourceNotFoundException, BusinessRuleException {
        var titulo="Titulo";
        Page<Topico>page= buildPage();
        when(topicoRepository.findAllByTitle(titulo,buildPageRequest()))
                .thenReturn(page);
        ResponseEntity<List<Topico>>responseEntity=
                readTopicoService.getAllByTitle(
                    titulo,queryPageable
                );
        var topicoActual=responseEntity.getBody().get(0);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(topico, topicoActual);

        verify(topicoRepository, times(1)).findAllByTitle(titulo,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(TITULO:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForTitle() throws BusinessRuleException {
        var titulo = "";
        assertThrows(
                BusinessRuleException.class,
                () ->readTopicoService.getAllByTitle(
                        titulo,queryPageable
                )
        );
        verify(topicoRepository,never()).findAllByTitle(titulo,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(TITULO:numerico)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForTitleWithNumbers() throws BusinessRuleException {
        var titulo = "12345";
        assertThrows(
                BusinessRuleException.class,
                () ->readTopicoService.getAllByTitle(
                        titulo,queryPageable
                )
        );
        verify(topicoRepository,never()).findAllByTitle(titulo,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Topico>(TITULO)")
    void itShouldThrowAResourceNotFoundExceptionWhenSearchingForTitle() throws BusinessRuleException {
        var titulo = "ascasc";
        assertThrows(
                ResourceNotFoundException.class,
                ()-> readTopicoService.getAllByTitle(
                        titulo,queryPageable
                ));
        verify(topicoRepository,times(1)).findAllByTitle(titulo,buildPageRequest());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(STATUS_TOPICO)")
    void itShouldBeAbleToGetATopicByStatus() throws ResourceNotFoundException,BusinessRuleException{
        var status = "No respondido";
        StatusTopico statusTopico= formattedStatus(status);

        Page<Topico>page= buildPage();
        when(topicoRepository.findAllByStatus(statusTopico,buildPageRequest()))
                .thenReturn(page);
        ResponseEntity<List<Topico>>responseEntity=
                readTopicoService.getAllByStatus(
                    status,queryPageable
                );
        var topicoActual=responseEntity.getBody().get(0);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(topico,topicoActual);

        verify(topicoRepository, times(1)).findAllByStatus(statusTopico,buildPageRequest());
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(STATUS_TOPICO:vacio)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForStatus(){
        var status = "";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByStatus(
                    status,queryPageable
             ));
        verify(topicoRepository,never()).findAllByStatus(any(StatusTopico.class), any(PageRequest.class));
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(STATUS_TOPICO:numerico)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForStatusWithNumbers(){
        var status = "12345";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByStatus(
                        status,queryPageable
                ));
        verify(topicoRepository,never()).findAllByStatus(any(StatusTopico.class), any(PageRequest.class));
    }
    @Test
    @DisplayName("Lanza_ResourceNotFoundException:Lista<Topico>(STATUS_TOPICO)")
    void itShouldThrowAResourceNotFoundExceptionWhenSearchingForStatus() throws BusinessRuleException {
        var status = "Cerrado";
        StatusTopico statusTopico= formattedStatus(status);
        when(topicoRepository.findAllByStatus(statusTopico,buildPageRequest()))
                .thenReturn(Page.empty());
        assertThrows(
                ResourceNotFoundException.class,
                ()-> readTopicoService.getAllByStatus(
                    status,queryPageable
        ));
        verify(topicoRepository,times(1)).findAllByStatus(any(StatusTopico.class),any(PageRequest.class));
    }
    @Test
    @DisplayName("Lanza_BusinessRuleException:Lista<Topico>(STATUS_TOPICO:No-Enum)")
    void itShouldThrowABusinessRuleExceptionWhenSearchingForBadStatus(){
        var status = "Test";
        assertThrows(
                BusinessRuleException.class,
                ()-> readTopicoService.getAllByStatus(
                    status,queryPageable
        ));
        verify(topicoRepository,never()).findAllByStatus(any(StatusTopico.class),any(PageRequest.class));
    }
}