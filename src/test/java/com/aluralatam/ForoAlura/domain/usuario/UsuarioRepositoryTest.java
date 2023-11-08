package com.aluralatam.ForoAlura.domain.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class UsuarioRepositoryTest{
    @Autowired
    private UsuarioRepository usuarioRepository;
    private final String nombre="Rubby";
    private final String apellido="Test";
    private final String pais="Argentina";
    private final String email="rubbytest@email.com";
    private final Pageable pageable = PageRequest.of(0, 15);
    @BeforeEach
    void setUp() {
        CreateDatoPersonalDTO datosPersonalesDto=new CreateDatoPersonalDTO(nombre,apellido,LocalDate.of(1998,1,16),pais,"Córdoba","Capital");
        CreateUsuarioDTO dto=new CreateUsuarioDTO(datosPersonalesDto,email,"contra_12345","contra_12345");
        Usuario usuario=new Usuario(dto);
        Usuario user=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Rubby")
                        .apellido("Prueba")
                        .fechaNacimiento(LocalDate.of(2000, 5, 25))
                        .pais("Argentina")
                        .provincia("Cordoba")
                        .localidad("Localidad")
                        .build())
                .email("rubyprueba@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario u=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Rubby")
                        .apellido("Nachweisen")
                        .fechaNacimiento(LocalDate.of(2000, 6, 18))
                        .pais("Argentina")
                        .provincia("Buenos Aires")
                        .localidad("Localidad")
                        .build())
                .email("rubynachweisen@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario us=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Poroto")
                        .apellido("Test")
                        .fechaNacimiento(LocalDate.of(2000, 3, 13))
                        .pais("Argentina")
                        .provincia("Rio Negro")
                        .localidad("Localidad")
                        .build())
                .email("porototest@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario usu=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Son")
                        .apellido("Test")
                        .fechaNacimiento(LocalDate.of(2002, 3, 23))
                        .pais("Argentina")
                        .provincia("Cordoba")
                        .localidad("Rio Ceballos")
                        .build())
                .email("sontest@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        usuarioRepository.save(usuario);
        usuarioRepository.save(user);
        usuarioRepository.save(u);
        usuarioRepository.save(us);
        usuarioRepository.save(usu);
    }
    @AfterEach
    void tearDown(){
        usuarioRepository.deleteAll();
    }
    @Test
    @DisplayName("Existe_Usuario(APELLIDO)")
    void itShouldBeAbleToCheckIfUserExistsBySurname(){
        boolean expected=usuarioRepository.existsByApellido(apellido);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("No_Existe_Usuario(APELLIDO)")
    void itShouldBeAbleToCheckThatUserDoesntExistBySurname() {
        var surname="Apellido";
        boolean expected=usuarioRepository.existsByApellido(surname);
        assertThat(expected).isFalse();
    }
    @Test
    @DisplayName("Existe_Usuario(NOMBRE)")
    void itShouldBeAbleToCheckIfUserExistsByName() {
        boolean expected=usuarioRepository.existsByNombre(nombre);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("NO_Existe_Usuario(NOMBRE))")
    void itShouldBeAbleToCheckThatUserDoesntExistByName() {
        var name="Nombre";
        boolean expected=usuarioRepository.existsByNombre(name);
        assertThat(expected).isFalse();
    }
    @Test
    @DisplayName("DESACTIVA_Usuario(ID)")
    void itShouldBeAbleToCheckIfUserIsSetToInactive() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Usuario u=usuarioRepository.findById(id).get();
        assertFalse(u.isActivo());
    }
    @Test
    @DisplayName("DESACTIVA_Lista<Usuario>(Lista<Usuario>)")
    void itShouldBeAbleToCheckIfUserListIsSetAsInactive() {
        List<Usuario> users = usuarioRepository.findAll();
        usuarioRepository.setInactiveToUserList(users);
        usuarioRepository.findAll()
            .forEach(
                course -> assertFalse(course.isActivo()));
    }
    @Test
    @DisplayName("(ACTIVOS)Retorna_Page<Usuario>(NOMBRE)")
    void findAllActiveUsersByName(){
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByNameOrSurname(nombre, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                        .getDato()
                        .getNombre()
                        .contains(nombre)));
        assertTrue(usuarios.stream().allMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("(ACTIVOS)Retorna_Page<Usuario>(APELLIDO)")
    void findAllActiveUsersBySurname(){
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByNameOrSurname(apellido, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getApellido()
                                .contains(apellido)));
        assertTrue(usuarios.stream().allMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("Retorna_Page<Usuario>(NOMBRE)")
    void findAllUsersByName() {
        Page<Usuario> usuarios= usuarioRepository.findAllUsersByNameOrSurname(nombre,pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(3, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getNombre()
                                .contains(nombre)));
    }
    @Test
    @DisplayName("Retorna_Page<Usuario>(APELLIDO)")
    void findAllUsersBySurame(){
        Page<Usuario> usuarios= usuarioRepository.findAllUsersByNameOrSurname(apellido,pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(3, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario-> usuario
                                .getDato()
                                .getApellido()
                                .contains(apellido)));
    }
    @Test
    @DisplayName("(INACTIVOS)Retorna_Page<Usuario>(NOMBRE)")
    void findAllInactiveUsersByName() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("rubynachweisen@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByNameOrSurname(nombre, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getNombre()
                                .contains(nombre)));
        assertTrue(usuarios.stream().noneMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("(INACTIVOS)Retorna_Page<Usuario>(APELLIDO)")
    void findAllInactiveUsersBySurname() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("porototest@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByNameOrSurname(apellido, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getApellido()
                                .contains(apellido)));
        assertTrue(usuarios.stream().noneMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("(ACTIVOS)Retorna_Page<Usuario>(PAIS)")
    void itShouldBeAbleToFindAllActiveUsersByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByCountry(pais, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(4, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getPais()
                                .contains(pais)));
        assertTrue(usuarios.stream().allMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("(INACTIVOS)Retorna_Page<Usuario>(PAIS)")
    void itShouldBeAbleToFindAllInactiveUsersByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("porototest@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByCountry(pais, pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario -> usuario
                                .getDato()
                                .getPais()
                                .contains(pais)));
        assertTrue(usuarios.stream().noneMatch(
                Usuario::isActivo));
    }
    @Test
    @DisplayName("Retorna_Page<Usuario>(PAIS)")
    void itShouldBeAbleToFindAllUsersByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllByCountry(pais,pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(5, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        usuario-> usuario
                                .getDato()
                                .getPais()
                                .contains(pais)));
    }
    @Test
    @DisplayName("Retorna_Page<Usuario>(TRUE)")
    void itShouldBeAbleToSearchUsersByActivo() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.searchByActivo(true,pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(4, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        Usuario::isActivo));
    }
    @Test
    @DisplayName("Retorna_Page<Usuario>(False)")
    void itShouldBeAbleToSearchUsersByInactivo() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Long id2=usuarioRepository.findByEmail("porototest@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.searchByActivo(false,pageable);
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .noneMatch(
                        Usuario::isActivo));
    }
}