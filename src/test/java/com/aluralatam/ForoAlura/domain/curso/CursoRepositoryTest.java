package com.aluralatam.ForoAlura.domain.curso;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
@DataJpaTest
public class CursoRepositoryTest{
    @Autowired
    private CursoRepository cursoRepository;
    private Curso curso;
    private CUCursoDto dto;
    @BeforeEach
    void setUp() {
        var nombre="Java";
        var categoria="Star-A1";
        dto = new CUCursoDto(nombre, categoria);
        curso=Curso.builder()
                .id(1L)
                .nombre("Java")
                .categoria("Star-A1")
                .inactivo(false)
                .build();
    }

    @AfterEach
    void tearDown(){
        cursoRepository.deleteAll();
    }
    @Test
    @DisplayName("Existe_Curso(Nombre-Categoria))")
    void itShouldCheckIfCoursesExistByNameAndCategory(){
        var nombre="Java";
        var categoria="Star-A1";
        cursoRepository.save(curso);
        boolean expected = cursoRepository.existsByNombreAndCategoria(nombre, categoria);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("No_Existe_Curso(Nombre-Categoria))")
    void itShouldCheckThatCoursesDoNotExistByNameAndCategory(){
        var nombre="nombreTest";
        var categoria="categoriaTest";
        boolean expected= cursoRepository.existsByNombreAndCategoria(nombre,categoria);
        assertThat(expected).isFalse();
    }
    @Test
    @DisplayName("Existe_Curso(Nombre)")
    void itShoulCheckIfCourseExistsByName(){
        var nombre="Java";
        cursoRepository.save(curso);
        boolean expected = cursoRepository.existsByNombre(nombre);
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("No_Existe_Curso(Nombre))")
    void itShouldCheckThatCourseDoesntExistsByName(){
        var nombre="nombreTest";
        boolean expected= cursoRepository.existsByNombre(nombre);
        assertThat(expected).isFalse();
    }
    @Test
    @DisplayName("Retorna_Page<Curso>(Nombre))")
    void itShouldFindCoursesByName(){
        String nombreCurso = "Java";
        cursoRepository.save(curso);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Curso> cursos= cursoRepository.search(nombreCurso, pageable);
        assertFalse(cursos.isEmpty());
        assertTrue(cursos.stream()
                .allMatch(curso -> curso.getNombre()
                        .contains(nombreCurso)));
    }
    @Test
    @DisplayName("Retorna_Page<Curso>_Vacia(Nombre))")
    void itShouldCheckThatNoCoursesAreFoundByName() {
        var nombre="nombre";
        cursoRepository.save(curso);
        Pageable pageable = PageRequest.of(0, 3);
        Page<Curso> cursos= cursoRepository.search(nombre, pageable);
        assertTrue(cursos.isEmpty());
    }
}