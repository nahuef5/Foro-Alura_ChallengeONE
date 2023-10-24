package com.aluralatam.ForoAlura.integrations.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.tools.Message;
import com.aluralatam.ForoAlura.global.tools.Response;
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
import java.util.List;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class ModifyControllerTest{
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

    private Usuario seedUsuarioInDDBB(){
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
        return repository.save(usuario);
    }

    @Test
    @DisplayName("Update-Data-Usuario+HttpStatus")
    public void itShouldBeAbleToUpdateUserAndReturnResponseEntity()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        final Long id=usuario.getId();
        //nombre
        var names=usuario.getDato().getNombre();
        String [] nameParts=names.split(" ");
        var firstName=nameParts[0];
        //apellido
        var lastnames=usuario.getDato().getApellido();
        String [] lastnameParts=lastnames.split(" ");
        var surname=lastnameParts[0];
        //dto
        UpdateDatoPersonalDTO dto=new UpdateDatoPersonalDTO(
            firstName,
            surname,
            FAKER.country().capital(),
            FAKER.address().city()
        );
        var jsonDTO=objectMapper.writeValueAsString(dto);
        MvcResult result=mockMvc.perform(put(BASE_URL+"/update-data/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDTO)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO MODIFICADO EXITOSAMENTE."))
                .andReturn()
        ;
        var jsonResponse = result.getResponse().getContentAsString();
        Response response = objectMapper.readValue(jsonResponse, Response.class);
        List<Usuario> usuarioList=repository.findAll();

        //asserts
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals(Message.UPDATED, response.getRespuesta());
    }
}