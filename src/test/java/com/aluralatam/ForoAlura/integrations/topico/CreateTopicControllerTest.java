package com.aluralatam.ForoAlura.integrations.topico;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.CreateTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
@Transactional
public class CreateTopicControllerTest {
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
    private CreateTopicDto createDTO(){
        var titulo=FAKER.programmingLanguage().name();
        var mensaje=FAKER.lorem().characters(20,100);
        Usuario usuario=seedUsuarioInDDBB();
        var idUser=usuario.getId();
        Curso curso=usuario.getCursos().get(0);
        var idCourse=curso.getId();
        return new CreateTopicDto(
                titulo,
                mensaje,
                idUser,
                idCourse
        );
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Topico")
    public void itShouldBeAbleToReturnResponseEntityWithTheCreatedTopic()throws Exception{
        CreateTopicDto dto=createDTO();
        var jsonDTO=objectMapper.writeValueAsString(dto);
        MvcResult result=mockMvc.perform(post(BASE_URL+"/ask")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDTO)
        )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var contentAsString=result.getResponse().getContentAsString();
        Topico response=objectMapper.readValue(contentAsString,Topico.class);
        assertTrue(response instanceof Topico);
    }
}