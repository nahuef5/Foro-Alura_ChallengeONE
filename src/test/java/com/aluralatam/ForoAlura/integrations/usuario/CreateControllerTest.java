package com.aluralatam.ForoAlura.integrations.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.tools.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.*;
import org.springframework.util.StringUtils;
import java.time.*;
import java.util.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class CreateControllerTest{
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final Faker FAKER = new Faker();
    private final String BASE_URL ="/api/v1/usuario";
    @Autowired
    private UsuarioRepository repository;
    private boolean isCountry(String pais){
        try{
            var replace=pais.toUpperCase().replace(" ","_");
            Countries.valueOf(replace);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
    private String generateValidCountry(){
        String pais;
        do{
            pais=FAKER.country().name();
        }while (!isCountry(pais));
        return pais;
    }
    @Test
    @DisplayName("Registra Usuario+HttpStatus")
    public void itShouldBeAbleToRegisterANewUserAndReturnResponseEntity()throws Exception{
        var names=String.format("%s %s", FAKER.name().firstName(),FAKER.name().firstName());
        var lastnames=String.format("%s %s", FAKER.name().lastName(),FAKER.name().lastName());
        Instant instant=FAKER.date().birthday(18,65).toInstant();
        final LocalDate born=instant.atZone(ZoneId.systemDefault()).toLocalDate();
        final String email=String.format("%s@email.com",
                StringUtils.trimAllWhitespace
                        (lastnames.trim().toLowerCase())
        );
        final String password="Password_15379";
        final CreateDatoPersonalDTO data=new CreateDatoPersonalDTO
                (names,
                 lastnames,
                 born,
                 generateValidCountry(),
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
        var jsonDTO=objectMapper.writeValueAsString(createUser);
        mockMvc.perform(post(BASE_URL+"/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.httpStatus").value("CREATED"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO CREADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();

        List<Usuario> usuarioList=repository.findAll();
        assertThat(usuarioList).usingElementComparatorIgnoringFields("id")
                .contains(usuario);
    }
}