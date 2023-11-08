package com.aluralatam.ForoAlura.integrations.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class ModifyUserControllerTest {
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
        var names=String.format(
                "%s %s",
                FAKER.name().firstName(),
                FAKER.name().firstName()
        );
        var lastnames=String.format(
                "%s %s",
                FAKER.name().lastName(),
                FAKER.name().lastName()
        );
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
    private List<Long> generateUsersList(){
        Usuario usuario=seedUsuarioInDDBB();
        Usuario user=seedUsuarioInDDBB();
        Usuario us=seedUsuarioInDDBB();
        Usuario u=seedUsuarioInDDBB();
        return Arrays.asList(usuario.getId(),user.getId(),us.getId(),u.getId());
    }
    private Usuario getUser(Long id){
        return repository.findById(id).get();
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(EDIT-DatoPersonal)")
    public void itShouldBeAbleToUpdateUserAndReturnResponseEntity()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        final Long id=usuario.getId();
        //nombre
        var names=usuario.getDato().getNombre();
        String [] nameParts=names.split(" ");
        final String firstName=nameParts[0];
        //apellido
        var lastnames=usuario.getDato().getApellido();
        String [] lastnameParts=lastnames.split(" ");
        final String surname=lastnameParts[0];
        //capital
        final String state=FAKER.address().state();
        final String city=FAKER.address().city();
        //dto
        UpdateDatoPersonalDTO dto=new UpdateDatoPersonalDTO(
            firstName,
            surname,
            state,
            city
        );
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(put(BASE_URL+"/update-data/{id}",id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonDTO)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO MODIFICADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();
        Usuario updated=repository.findById(id).get();
        assertEquals(firstName, updated.getDato().getNombre());
        assertEquals(surname,updated.getDato().getApellido());
        assertEquals(state,updated.getDato().getProvincia());
        assertEquals(city,updated.getDato().getLocalidad());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(NEW-Password")
    public void itShouldBeAbleToGenerateANewPassword()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        var email=usuario.getEmail();
        var password="Abcdef_12345";
        NuevaContrasena dto=new NuevaContrasena(email,password,password);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch(BASE_URL+"/new-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO MODIFICADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();
        Usuario updated=repository.findByEmail(email).get();
        assertEquals(password, updated.getContrasena());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(DisableOne)")
    public void itShouldBeAbleToDisableAccount()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        final Long id=usuario.getId();
        RemoveUsuarioDto dto=new RemoveUsuarioDto(id,true);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch(BASE_URL+"/disable-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("ELIMINADO EXITOSAMENTE."))
                .andDo(print())
                .andReturn();
        Usuario updated=repository.findById(id).get();
        assertEquals(false, updated.isActivo());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(DisableMany)")
    public void itShouldBeAbleToDisableAccounts()throws Exception{
        List<Long>ids=generateUsersList();
        var remove=true;
        var id1=ids.get(0);
        var id2=ids.get(1);
        var id3=ids.get(2);
        var id4=ids.get(3);
        RemoveListaUsuariosDto dto=new RemoveListaUsuariosDto(ids,remove);
        var jsonDTO=objectMapper.writeValueAsString(dto);
        mockMvc.perform(patch(BASE_URL+"/disable-users")
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
                u->assertFalse(u.isActivo())
        );
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Response(ReactiveAccount)")
    public void itShouldBeAbleToReactivateAccount()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        final String email=usuario.getEmail();
        repository.setInactiveToUser(usuario.getId());
        ByParameterDto mailDto=new ByParameterDto(email);
        var jsonDTO=objectMapper.writeValueAsString(mailDto);
        mockMvc.perform(patch(BASE_URL+"/reactive-users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDTO)
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.httpStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.respuesta").value("RECURSO REACTIVADO EXITOSAMENTE"))
                .andDo(print())
                .andReturn();

        Usuario updated=repository.findByEmail(email).get();
        assertTrue(updated.isActivo());
    }
}