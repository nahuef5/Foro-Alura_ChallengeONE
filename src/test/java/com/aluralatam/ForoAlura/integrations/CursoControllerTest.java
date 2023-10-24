package com.aluralatam.ForoAlura.integrations;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.CursoServiceImpl;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.global.tools.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.*;
import com.github.javafaker.Faker;
import org.springframework.test.web.servlet.MvcResult;
import java.util.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class CursoControllerTest{
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    //Para generar datos falsos
    private final Faker FAKER = new Faker();
    //URL base para las solicitudes
    private final String BASE_URL = "/api/v1/curso";
    @Autowired
    private CursoRepository cursoRepository;
    //valores predeterminados para paginacion
    private final String PAGE_NUMBER ="1";
    private final String PAGE_SIZE ="15";
    private String nameGenerator(){
        return FAKER.name().firstName();
    }
    private String categoryGenerator(){
        return FAKER.regexify("[A-Za-z]{3,30}");
    }
    //metodo para sembrar curso en la ddbb
    private Curso seedCursoInDDBB(){
        var nombre= nameGenerator();
        var categoria= categoryGenerator();
        CUCursoDto dto=new CUCursoDto(nombre, categoria);
        Curso curso=new Curso(dto);
        return cursoRepository.save(curso);
    }
    @Test
    @DisplayName("Retorna Lista por nombre con paginacion")
    public void itShouldBeAbleToReturnListOfCursosByNombreWithPagination()throws Exception{
        //guarda un curso en la ddbb
        Curso curso=seedCursoInDDBB();
        //obtenemos su nombre
        var nombre=curso.getNombre();
        //generamos el dto para buscar por nomre
        NombreDto dto=new NombreDto(nombre);
        //get (search) al endpoint
        MvcResult  result=mockMvc.perform(get(BASE_URL+"/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .param("pageNumber", PAGE_NUMBER)
                .param("pageSize", PAGE_SIZE)
        )
                .andExpect(status().isFound())
                .andDo(print())
                .andReturn()
        ;
        //verifica la respuesta
        var jsonResponse=result.getResponse().getContentAsString();
        List<Curso>actualCursos=objectMapper.readValue(jsonResponse,new TypeReference<List<Curso>>(){});
        //asserts
        assertFalse(actualCursos.isEmpty());
        actualCursos.forEach(course -> assertFalse(course.isInactivo()));
        actualCursos.forEach(course -> assertEquals(nombre,course.getNombre()));
    }
    @Test
    @DisplayName("Obtiene lista de cursos")
    public void itShouldBeAbleReturnListOfCursosByPagination()throws Exception{
        //get all al endpoint
        MvcResult result=mockMvc.perform(get(BASE_URL+"/all")
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE))
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn()
        ;
        //verificar respuesta
        var jsonResponse=result.getResponse().getContentAsString();
        List<Curso>actualCursos=objectMapper.readValue(jsonResponse,new TypeReference<List<Curso>>(){});
        //asserts
        assertFalse(actualCursos.isEmpty());
        actualCursos.forEach(curso -> assertFalse(curso.isInactivo()));
    }
    @Test
    @DisplayName("Registra curso y retornar ResponseEntity")
    public void itShouldBeAbleToRegisterCursoAndReturnAResponseEntity()throws Exception {
        //crea dto
        var nombre = nameGenerator();
        var categoria=categoryGenerator();
        CUCursoDto dto=new CUCursoDto(nombre, categoria);
        //instancia de curso
        Curso nuevoCurso=new Curso(dto);
        //convierte json
        var jsonDTO=objectMapper.writeValueAsString(dto);
        //post al endpoint
        MvcResult result=mockMvc.perform(post(BASE_URL+"/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDTO)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value("CREATED"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO CREADO EXITOSAMENTE."))
                .andReturn();
        //verifica el response
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        //lista para verificar que se haya guardado en la ddbb
        List<Curso> cursoList=cursoRepository.findAll();
        //asserts
        assertEquals(HttpStatus.CREATED, response.getHttpStatus());
        assertEquals(Message.CREATED, response.getRespuesta());
        assertThat(cursoList).usingElementComparatorIgnoringFields("id")
                .contains(nuevoCurso);
    }
    @Test
    @DisplayName("Actualiza curso y retorna ResponseEntity")
    public void itShouldBeAbleToUpdateCursoAndReturnAResponseEntity()throws Exception{
        //siembra curso
        Curso curso= seedCursoInDDBB();
        //obtiene su id y genera dto
        Long id=curso.getId();
        var nuevoNombre=nameGenerator();
        var nuevaCategoria=categoryGenerator();
        CUCursoDto dto=new CUCursoDto(nuevoNombre,nuevaCategoria);
        //instancia de curso
        Curso nuevoCurso=new Curso(dto);
        //convierte json
        var jsonDTO=objectMapper.writeValueAsString(dto);
        //put en el endpoint
        MvcResult result=mockMvc.perform(put(BASE_URL+"/update/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDTO)
        )
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO MODIFICADO EXITOSAMENTE."))
                .andReturn()
        ;
        //verifica el response
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        //lista para verificar que se haya guardado en la ddbb
        List<Curso> cursoList=cursoRepository.findAll();
        //asserts
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(Message.UPDATED, response.getRespuesta());
        assertThat(cursoList).usingElementComparatorIgnoringFields("id")
                .contains(nuevoCurso);
    }
    @Test
    @DisplayName("Activa curso y retornar ResponseEntity")
    public void itShouldBeAbleToActivateCursoAndReturnAResponseEntity()throws Exception{
        //siembra curso
        Curso curso= seedCursoInDDBB();
        //setea inactivo==true
        curso.setInactivo(true);
        cursoRepository.save(curso);
        //instacia para luego  assertear
        Curso cursoDesactivado=curso;
        Long id =curso.getId();
        //patch en el endpoint
        MvcResult result=mockMvc.perform(patch(BASE_URL+"/active/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO REACTIVADO EXITOSAMENTE")
                )
                .andDo(print())
                .andReturn()
        ;
        //verifica el response
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        //lista para verificar que se haya guardado en la ddbb
        List<Curso> cursoList=cursoRepository.findAll();
        //asserts
        assertEquals(HttpStatus.ACCEPTED, response.getHttpStatus());
        assertEquals("RECURSO REACTIVADO EXITOSAMENTE", response.getRespuesta());
        assertThat(cursoList).usingElementComparatorIgnoringFields("id")
            .doesNotContain(cursoDesactivado);
    }
    @Test
    @DisplayName("Desactiva curso y retorna ResponseEntity")
    public void itShouldBeAbleToDisableCursoAndReturnAResponseEntity()throws Exception{
        //siembra curso
        Curso curso= seedCursoInDDBB();
        //valores para el dto
        Long id =curso.getId();
        boolean inactivo=true;
        //dto
        DeleteOrDesableCursoDto disableDto=new DeleteOrDesableCursoDto(id,inactivo);
        //delete en el endpoint
        MvcResult result=mockMvc.perform(delete(BASE_URL+"/disable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(disableDto))
        )
        .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("ELIMINADO EXITOSAMENTE.")
                )
                .andDo(print())
                .andReturn()
        ;
        //verifica el response
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        //verificar que se haya guardado en la ddbb
        Curso cursoDesactivado = cursoRepository.findById(id).get();
        //asserts
        assertEquals(HttpStatus.ACCEPTED, response.getHttpStatus());
        assertEquals(Message.ELIMINATED, response.getRespuesta());
        assertThat(cursoDesactivado.isInactivo()).isEqualTo(inactivo);
    }
    @Test
    @DisplayName("Elimina curso retorna ResponseEntity")
    public void itShouldBeAbleToDeleteCursoAndReturnAResponseEntity()throws Exception{
        //siembra curso
        Curso curso= seedCursoInDDBB();
        //valores para el dto
        Long id =curso.getId();
        boolean inactivo=true;
        //dto
        DeleteOrDesableCursoDto deleteDto=new DeleteOrDesableCursoDto(id,inactivo);
        MvcResult result=mockMvc.perform(delete(BASE_URL+"/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteDto))
        )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("ELIMINADO EXITOSAMENTE.")
                ).andDo(print())
                .andReturn()
        ;
        //verifica el response
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        boolean existsCurso=cursoRepository.existsById(id);
        //asserts
        assertEquals(HttpStatus.ACCEPTED, response.getHttpStatus());
        assertEquals(Message.ELIMINATED, response.getRespuesta());
        assertFalse(existsCurso);
    }

    @Test
    @DisplayName("Obtiene curso por id y retorna ResponseEntity")
    public void itShouldBeAbleReturnAResponseEntityAndGetACursoById()throws Exception{
        //siembra un curso
        Curso curso= seedCursoInDDBB();
        //obtiene su id
        Long id=curso.getId();
        //get by id al endpoint
        MvcResult result=mockMvc.perform(get(BASE_URL+"/byId/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nombre").value(curso.getNombre()))
                .andExpect(jsonPath("$.categoria").value(curso.getCategoria()))
                .andExpect(jsonPath("$.inactivo").value(curso.isInactivo())
        )
                .andDo(print())
                .andReturn()
        ;
        //verifica la respuesta
        String contentAsString=result.getResponse().getContentAsString();
        Curso response=objectMapper.readValue(contentAsString,Curso.class);
        assertEquals(curso.hashCode(),response.hashCode());
    }
    @Test
    @DisplayName("Obtiene por nombre-categoria y retorna ResponseEntity")
    public void itShouldBeAbleReturnAResponseEntityAndGetACursoByNombreAndCategoria()throws Exception{
        //siembra el curso
        Curso curso= seedCursoInDDBB();
        //obtiene nombre y categoria para dto
        var nombre=curso.getNombre();
        var categoria=curso.getCategoria();
        CUCursoDto dto=new CUCursoDto(nombre,categoria);
        //get by nombre y categoria
        MvcResult result=mockMvc.perform(get(BASE_URL+"/byNombreAndCategoria")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
                .andExpect(status().isFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn()
        ;
        //verifica respuesta
        String contentAsString=result.getResponse().getContentAsString();
        Curso response=objectMapper.readValue(contentAsString,Curso.class);
        //asserts
        assertEquals(curso.getNombre(), response.getNombre());
        assertEquals(curso.getCategoria(), response.getCategoria());
    }
}