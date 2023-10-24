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
    private final String apellido="Gata";
    private final String pais="Argentina";
    private final String email="rubbyfunes@email.com";
    private final Pageable pageable = PageRequest.of(0, 15);
    @BeforeEach
    void setUp() {
        CreateDatoPersonalDTO datosPersonalesDto=new CreateDatoPersonalDTO(nombre,apellido,LocalDate.of(1998,1,16),pais,"Córdoba","Capital");
        CreateUsuarioDTO dto=new CreateUsuarioDTO(datosPersonalesDto,email,"contra_12345","contra_12345");
        Usuario usuario=new Usuario(dto);
        Usuario user=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Rubby")
                        .apellido("Funes")
                        .fechaNacimiento(LocalDate.of(2000, 5, 25))
                        .pais("Argentina")
                        .provincia("Cordoba")
                        .localidad("Localidad")
                        .build())
                .email("ruruby@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario u=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Rubby")
                        .apellido("Felina")
                        .fechaNacimiento(LocalDate.of(2000, 6, 18))
                        .pais("Argentina")
                        .provincia("Buenos Aires")
                        .localidad("Localidad")
                        .build())
                .email("rubyfelina@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario us=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Poroto")
                        .apellido("Gata")
                        .fechaNacimiento(LocalDate.of(2000, 3, 13))
                        .pais("Argentina")
                        .provincia("Rio Negro")
                        .localidad("Localidad")
                        .build())
                .email("porotogata@email.com")
                .contrasena("Xxxxx_12345")
                .activo(true)
                .build();
        Usuario usu=Usuario.builder().dato(DatoPersonal.builder()
                        .nombre("Son")
                        .apellido("Gata")
                        .fechaNacimiento(LocalDate.of(2002, 3, 23))
                        .pais("Argentina")
                        .provincia("Cordoba")
                        .localidad("Rio Ceballos")
                        .build())
                .email("songata@email.com")
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
    @DisplayName("Debe verificar que el APELLIDO de usuario EXISTE")
    void itShouldBeAbleToCheckIfUserExistsBySurname() {
        boolean expected=usuarioRepository.existsByApellido(apellido);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("Debe verificar que ese APELLIDO de usuario NO EXISTE")
    void itShouldBeAbleToCheckThatUserDoesntExistBySurname() {
        var surname="Apellido";
        boolean expected=usuarioRepository.existsByApellido(surname);
        assertThat(expected).isFalse();
    }
    @Test
    @DisplayName("Debe verificar ese NOMBRE de usuario EXISTE")
    void itShouldBeAbleToCheckIfUserExistsByName() {
        boolean expected=usuarioRepository.existsByNombre(nombre);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("Debe verificar ese NOMBRE de usuario NO EXISTE")
    void itShouldBeAbleToCheckThatUserDoesntExistByName() {
        var name="Nombre";
        boolean expected=usuarioRepository.existsByNombre(name);
        assertThat(expected).isFalse();
    }

    @Test
    @DisplayName("Debe verificar que DESACTIVA usuario por ID")
    void itShouldBeAbleToCheckIfUserIsSetToInactive() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Usuario u=usuarioRepository.findById(id).get();
        assertFalse(u.isActivo());
    }
    @Test
    @DisplayName("Debe verificar que DESACTIVA una LISTA de usuarios")
    void itShouldBeAbleToCheckIfUserListIsSetAsInactive() {

        List<Usuario> users = usuarioRepository.findAll();
        usuarioRepository.setInactiveToUserList(users);
        usuarioRepository.findAll()
                .forEach(
                    course -> assertFalse(course.isActivo())
                );
    }
    @Test
    @DisplayName("Debe verificar que obtiene USUARIOS ACTIVOS por NOMBRE")
    void findAllActiveUsersByName(){
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByNameOrSurname(nombre, pageable);
        //Verificaciones
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
    @DisplayName("Debe verificar que obtiene USUARIOS ACTIVOS por APELLIDO")
    void findAllActiveUsersBySurname(){
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByNameOrSurname(apellido, pageable);
        //Verificaciones
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
    @DisplayName("Debe verificar que obtiene USUARIOS por NOMBRE")
    void findAllUsersByName() {
        Page<Usuario> usuarios= usuarioRepository.findAllUsersByNameOrSurname(nombre,pageable);
        //Verificaciones
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
    @DisplayName("Debe verificar que obtiene USUARIOS por APELLIDO")
    void findAllUsersBySurame() {
        Page<Usuario> usuarios= usuarioRepository.findAllUsersByNameOrSurname(apellido,pageable);
        //Verificaciones
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
    @DisplayName("Debe verificar que obtiene USUARIOS INACTIVOS por NOMBRE")
    void findAllInactiveUsersByName() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("rubyfelina@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByNameOrSurname(nombre, pageable);
        //Verificaciones
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
    @DisplayName("Debe verificar que obtiene USUARIOS INACTIVOS por APELLIDO")
    void findAllInactiveUsersBySurname() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("porotogata@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByNameOrSurname(apellido, pageable);
        //Verificaciones
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
    void findAllActiveUsersByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllActiveUsersByCountry(pais, pageable);
        //Verificaciones
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
    void findAllInactiveUsersByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        Long id2=usuarioRepository.findByEmail("porotogata@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id);
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.findAllInactiveUsersByCountry(pais, pageable);
        //Verificaciones
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
    void findAllByCountry() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllByCountry(pais,pageable);
        //Verificaciones
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
    void searchByActivo() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.searchByActivo(true,pageable);
        //Verificaciones
        assertFalse(usuarios.isEmpty());
        assertEquals(4, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        Usuario::isActivo));
    }
    @Test
    void searchByInactivo() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Long id2=usuarioRepository.findByEmail("porotogata@email.com").get().getId();
        usuarioRepository.setInactiveToUser(id2);
        Page<Usuario> usuarios= usuarioRepository.searchByActivo(false,pageable);
        //Verificaciones
        assertFalse(usuarios.isEmpty());
        assertEquals(2, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .noneMatch(
                        Usuario::isActivo));
    }
    @Test
    void findAllAssets() {
        Page<Usuario> usuarios= usuarioRepository.findAllAssets(pageable);
        //Verificaciones
        assertFalse(usuarios.isEmpty());
        assertEquals(5, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        Usuario::isActivo));
    }
    @Test
    void findAllAssetsWithAUserInactivo() {
        Long id=usuarioRepository.findByEmail(email).get().getId();
        usuarioRepository.setInactiveToUser(id);
        Page<Usuario> usuarios= usuarioRepository.findAllAssets(pageable);
        //Verificaciones
        assertFalse(usuarios.isEmpty());
        assertEquals(4, usuarios.get().count());
        assertTrue(usuarios
                .stream()
                .allMatch(
                        Usuario::isActivo));
    }
}