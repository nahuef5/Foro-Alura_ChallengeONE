package com.aluralatam.ForoAlura.integrations.topico;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.respuesta.model.entity.Respuesta;
import com.aluralatam.ForoAlura.domain.respuesta.service.repository.RespuestaRepository;
import com.aluralatam.ForoAlura.domain.topico.model.dtos.ModifyTopicDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.ByParameterDto;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateDatoPersonalDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateUsuarioDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
@Transactional
public class ModifyTopicControllerTest {
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
    @Autowired
    private RespuestaRepository respuestaRepository;
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
    private Respuesta seedRespuestaInDDBB(){
        Topico topico=seedTopicoInDDBB();
        Usuario usuario=seedUsuarioInDDBB();
        Respuesta respuesta=new Respuesta(
                FAKER.lorem().characters(10,15),
                usuario,
                topico
        );
        var response=respuestaRepository.save(respuesta);
        usuario.addRespuestaToList(response);
        topico.addRespuestaToList(response);
        topicoRepository.save(topico);
        usuarioRepository.save(usuario);
        return response;
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Topico(EDIT)")
    public void itShouldBeAbleToReturnResponseEntityWithTheEditTopic()throws Exception{
        Topico topico=seedTopicoInDDBB();
        var id=topico.getId();
        ModifyTopicDto dto=new ModifyTopicDto(
                FAKER.programmingLanguage().name(),
                FAKER.lorem().characters(20,150)
        );
        var jsonDTO=objectMapper.writeValueAsString(dto);
        MvcResult result=mockMvc.perform(put(BASE_URL+"/editTopic/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var contentAsString=result.getResponse().getContentAsString();
        Topico response=objectMapper.readValue(contentAsString,Topico.class);
        assertTrue(response instanceof Topico);
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Topico(UPDATED_Status)")
    public void itShoulBeAbleToReturnResponseEntityWithTheUpdatedStatusTopico()throws Exception{
        var id=seedRespuestaInDDBB().getTopico().getId();
        var status="No Solucionado";
        ByParameterDto dto=new ByParameterDto(status);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        MvcResult result=mockMvc.perform(patch(BASE_URL+"/changeTopicStatus/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var contentAsString=result.getResponse().getContentAsString();
        Topico response=objectMapper.readValue(contentAsString,Topico.class);
        assertTrue(response instanceof Topico);
    }
}