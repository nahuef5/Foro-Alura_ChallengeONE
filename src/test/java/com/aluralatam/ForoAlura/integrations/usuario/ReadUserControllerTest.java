package com.aluralatam.ForoAlura.integrations.usuario;
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
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@TestPropertySource(locations="classpath:application-it.properties")
@AutoConfigureMockMvc
public class ReadUserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final Faker FAKER = new Faker();
    private final String BASE_URL ="/api/v1/usuario";
    @Autowired
    private UsuarioRepository repository;
    private final String PAIS="Argentina";
    private final String NOMBRE="Nahuel";
    private final String APELLIDO="Funes";
    private final String PAGE_NUMBER ="1";
    private final String PAGE_SIZE ="15";
    private Usuario seedUsuarioInDDBB(){
        var forEmail=String.format(
                "%s %s",
                FAKER.name().firstName(),
                FAKER.name().firstName());

        final String email=String.format("%s@email.com",
                StringUtils.trimAllWhitespace
                        (forEmail.trim().toLowerCase())
        );
        final String password="Contras_15379";
        final CreateDatoPersonalDTO data=new CreateDatoPersonalDTO
                (
                    NOMBRE,
                    APELLIDO,
                    LocalDate.of(1998,1,16),
                    PAIS,
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
    private void saveUsersToTest(){
        var i=0;
        while(i<=6){
            if(i<4){
                seedUsuarioInDDBB();
            }else {
                Usuario u=seedUsuarioInDDBB();
                repository.setInactiveToUser(u.getId());
            }
            i++;
        }
    }
    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Usuario(EMAIL)")
    public void itShouldBeAblegGetAUserByEmail()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        var email=usuario.getEmail();
        ByParameterDto dto=new ByParameterDto(email);
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getByEmail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        String contentAsString=result.getResponse().getContentAsString();
        Usuario response=objectMapper.readValue(contentAsString,Usuario.class);
        assertEquals(usuario.getEmail(), response.getEmail());
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Usuario(ID)")
    public void itShouldBeAblegGetAUserById()throws Exception{
        Usuario usuario=seedUsuarioInDDBB();
        var id=usuario.getId();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getById/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        String contentAsString=result.getResponse().getContentAsString();
        Usuario response=objectMapper.readValue(contentAsString,Usuario.class);
        assertEquals(usuario, response);
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(ACTIVO)Lista<Usuario>(Nombre-Apellido)")
    public void itShouldBeAbleToGetActiveUsersByNameOrLastWithPagination()throws Exception{
        saveUsersToTest();
        ByParameterDto dto=new ByParameterDto(NOMBRE);

        MvcResult  result=mockMvc.perform(get(BASE_URL+"/getAllActiveByNoS")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .param("pageNumber", PAGE_NUMBER)
                .param("pageSize", PAGE_SIZE)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});

        assertFalse(actual.isEmpty());
        assertEquals(4,actual.size());
        actual.forEach(user -> assertTrue(user.isActivo()));
        actual.forEach(user -> assertEquals(NOMBRE,user.getDato().getNombre()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(INACTIVO)Lista<Usuario>(Nombre-Apellido)")
    public void itShouldBeAbleToGetInactiveUsersByNameOrLastWithPagination()throws Exception{
        saveUsersToTest();
        ByParameterDto dto=new ByParameterDto(APELLIDO);

        MvcResult  result=mockMvc.perform(get(BASE_URL+"/getAllInactiveByNoS")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});

        assertFalse(actual.isEmpty());
        assertEquals(3,actual.size());
        actual.forEach(user -> assertFalse(user.isActivo()));
        actual.forEach(user -> assertEquals(APELLIDO,user.getDato().getApellido()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(TRUE)")
    public void itShouldBeAblegGetAUsersByActivoEqualTrue()throws Exception{
        final boolean activo=true;
        saveUsersToTest();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByActivo/{activo}",activo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});

        assertFalse(actual.isEmpty());
        assertEquals(4,actual.size());
        actual.forEach(user -> assertTrue(user.isActivo()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(FALSE)")
    public void itShouldBeAblegGetAUsersByActivoEqualFalse()throws Exception{
        final boolean activo=false;
        saveUsersToTest();
        MvcResult result=mockMvc.perform(get(BASE_URL+"/getAllByActivo/{activo}",activo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});

        assertFalse(actual.isEmpty());
        assertEquals(3,actual.size());
        actual.forEach(user -> assertFalse(user.isActivo()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:Lista<Usuario>(PAIS)")
    public void itShouldBeAbleToGetUsersByCountryWithPagination()throws Exception{
        saveUsersToTest();
        ByParameterDto dto=new ByParameterDto(PAIS);

        MvcResult  result=mockMvc.perform(get(BASE_URL+"/getAllByCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});

        assertFalse(actual.isEmpty());
        assertEquals(7,actual.size());
        actual.forEach(user -> assertEquals(PAIS,user.getDato().getPais()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(ACTIVO)Lista<Usuario>(PAIS)")
    public void itShouldBeAbleToGetActiveUsersByCountryWithPagination()throws Exception{
        ByParameterDto dto=new ByParameterDto(PAIS);
        saveUsersToTest();

        MvcResult  result=mockMvc.perform(get(BASE_URL+"/getAllActiveByCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(4,actual.size());
        actual.forEach(user->assertTrue(user.isActivo()));
        actual.forEach(user -> assertEquals(PAIS,user.getDato().getPais()));
    }
    @Test
    @DisplayName("Retorna_ResponseEntity_Body:(INACTIVO)Lista<Usuario>(PAIS)")
    public void itShouldBeAbleToGetInactiveUsersByCountryWithPagination()throws Exception{
        ByParameterDto dto=new ByParameterDto(PAIS);
        saveUsersToTest();
        MvcResult  result=mockMvc.perform(get(BASE_URL+"/getAllInactiveByCountry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .param("pageNumber", PAGE_NUMBER)
                        .param("pageSize", PAGE_SIZE)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        var jsonResponse=result.getResponse().getContentAsString();
        List<Usuario> actual=objectMapper.readValue(jsonResponse,new TypeReference<List<Usuario>>(){});
        assertFalse(actual.isEmpty());
        assertEquals(3,actual.size());
        actual.forEach(user->assertFalse(user.isActivo()));
        actual.forEach(user -> assertEquals(PAIS,user.getDato().getPais()));
    }
}