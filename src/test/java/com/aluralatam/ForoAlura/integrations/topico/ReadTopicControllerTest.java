package com.aluralatam.ForoAlura.integrations.topico;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
@Transactional
public class ReadTopicControllerTest {
   @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final Faker FAKER = new Faker();
    private final String BASE_URL ="/api/v1/topico";
    @Autowired
    private TopicoRepository topicoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CursoRepository cursoRepository;
    private final String PAGE_NUMBER ="1",PAGE_SIZE ="15";

    @AfterEach
    void tearDown() {
        topicoRepository.deleteAll();
        usuarioRepository.deleteAll();
        cursoRepository.deleteAll();
    }

    private Curso seedCursoInDDBB(){
        var nombre= FAKER.programmingLanguage().name();
        var categoria= FAKER.regexify("[A-Za-z]{3,30}");;
        CUCursoDto dto=new CUCursoDto(nombre, categoria);
        Curso curso=new Curso(dto);
        return cursoRepository.save(curso);
    }
    private Usuario seedUsuarioInDDBB(){
        var nombre=FAKER.name().firstName();
        var apellido=FAKER.name().lastName();
        var forEmail=String.format(
                "%s %s",
                nombre,
                apellido);

        final String email=String.format("%s@email.com",
                StringUtils.trimAllWhitespace
                        (forEmail.trim().toLowerCase())
        );
        final String password="Contras_15379";
        final CreateDatoPersonalDTO data=new CreateDatoPersonalDTO
                (
                        nombre,
                        apellido,
                        LocalDate.of(1998,1,16),
                        "Colombia",
                        FAKER.address().state(),
                        FAKER.address().city()
                );
        final CreateUsuarioDTO createUser=new CreateUsuarioDTO(
                data,
                email,
                password,
                password
        );
        Usuario usuario=new Usuario(createUser);
        var u= usuarioRepository.save(usuario);
        Curso curso=seedCursoInDDBB();
        u.addCursoToList(curso);
        return usuarioRepository.save(u);
    }
    private Topico seedTopicoInDDBB(){
        var title=FAKER.programmingLanguage().name();
        var message=FAKER.lorem().characters(10,100);
        var usuario =seedUsuarioInDDBB();
        var curso=usuario.getCursos().get(0);
        Topico topico =new Topico(
                title,
                message,
                usuario,
                curso
        );
        Topico topic=topicoRepository.save(topico);
        usuario.addTopicoToList(topic);
        curso.addTopicoToList(topic);
        usuarioRepository.save(usuario);
        cursoRepository.save(curso);
        return topic;
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Topico(ID)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicByID()throws Exception{
        Topico topico =seedTopicoInDDBB();
        var id=topico.getId();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getById/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        String contentAsString=result.getResponse().getContentAsString();
        Topico response=objectMapper.readValue(contentAsString,Topico.class);
        assertEquals(topico, response);
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Topico(TITLE)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicByTitle()throws Exception{
        Topico topico =seedTopicoInDDBB();
        var title=topico.getTitulo();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getByTitle/{titulo}",title)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var contentAsString =result.getResponse().getContentAsString();
        var response=objectMapper.readValue(contentAsString,Topico.class);
        assertEquals(topico.getTitulo(),response.getTitulo());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(EMAIL)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicListByEmail()throws Exception{
        Topico topico=seedTopicoInDDBB();
        var email=topico.getAutor().getEmail();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByUser/{email}",email)
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNumber", PAGE_NUMBER)
                .param("pageSize", PAGE_SIZE)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Topico> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Topico>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(1,actual.size());
        actual.forEach(topic->assertEquals(topico,topic));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(CourseName)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicListByCourseName()throws Exception{
        Topico topico=seedTopicoInDDBB();
        var email=topico.getCurso().getNombre();
        ByParameterDto dto=new ByParameterDto(email);

        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByCourse")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Topico> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Topico>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(1,actual.size());
        actual.forEach(topic->assertEquals(topico,topic));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(StatusTopico)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicListByStatus()throws Exception{
        Topico topico=seedTopicoInDDBB();
        var status=topico.getStatus().name().replace("_", " ");
        ByParameterDto dto=new ByParameterDto(status);

        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByStatusTopico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Topico> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Topico>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(1,actual.size());
        actual.forEach(topic->assertEquals(topico,topic));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Topico>(TITLE)")
    public void itShouldBeAbleToReturnResponseEntityWithATopicListByTitle()throws Exception{
        Topico topico=seedTopicoInDDBB();
        var titulo=topico.getTitulo();
        ByParameterDto dto=new ByParameterDto(titulo);

        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByTitulo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Topico> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Topico>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(1,actual.size());
        actual.forEach(topic->assertEquals(topico,topic));
    }
}