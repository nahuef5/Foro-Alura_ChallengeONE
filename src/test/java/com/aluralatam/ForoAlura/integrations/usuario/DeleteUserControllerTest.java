package com.aluralatam.ForoAlura.integrations.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class DeleteUserControllerTest {
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
        //setear activo a false
        usuario.setActivo(false);
        return repository.save(usuario);
    }
    private List<Long> generateUsersList(){
        Usuario usuario=seedUsuarioInDDBB();
        Usuario user=seedUsuarioInDDBB();
        Usuario us=seedUsuarioInDDBB();
        Usuario u=seedUsuarioInDDBB();
        return Arrays.asList(usuario.getId(),user.getId(),us.getId(),u.getId());
    }
    private Usuario getUser(Long id){
        return repository.findById(id).orElse(null);
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(DeleteOne)")
    public void itShouldBeAbleToDeleteAccount()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        final Long id=usuario.getId();

        RemoveUsuarioDto dto=new RemoveUsuarioDto(id,true);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(delete(BASE_URL+"/delete-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("ELIMINADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();
        Usuario deleted=repository.findById(id).orElse(null);
        assertNull(deleted);
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(DeleteMany)")
    public void itShouldBeAbleToDisableAccounts()throws Exception{
        List<Long>ids=generateUsersList();
        var remove=true;
        var id1=ids.get(0);
        var id2=ids.get(1);
        var id3=ids.get(2);
        var id4=ids.get(3);

        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,remove);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(delete(BASE_URL+"/delete-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("ELIMINADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();
        List<Usuario>users=Arrays.asList(
                getUser(id1),
                getUser(id2),
                getUser(id3),
                getUser(id4)
        );
        users.forEach(
                u->assertNull(u)
        );
    }
}