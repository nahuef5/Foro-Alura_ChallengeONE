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
        //curso = new Curso(dto);
    }

    @AfterEach
    void tearDown(){
        cursoRepository.deleteAll();
    }
    @Test
    void debeCheckearSiNombreYCategoriaExiste(){
        //given
        var nombre="Java";
        var categoria="Star-A1";
        cursoRepository.save(curso);
        //when
        boolean expected = cursoRepository.existsByNombreAndCategoria(nombre, categoria);
        //then
        assertThat(expected).isTrue();
    }
    @Test
    @DisplayName("Prueba que verifica que no existe un curso con ese nombre y categoria")
    void debeCheckearQueNoExisteNombreYCategoria(){
        //given
        var nombre="nombreTest";
        var categoria="categoriaTest";
        //when
        boolean expected= cursoRepository.existsByNombreAndCategoria(nombre,categoria);
        //then
        assertThat(expected).isFalse();
    }
    @Test
    void debeCheckearSiNombreExiste(){
        //given
        var nombre="Java";
        cursoRepository.save(curso);
        //when
        boolean expected = cursoRepository.existsByNombre(nombre);
        //then
        assertThat(expected).isTrue();
    }
    @Test
    void debeCheckearQueNoExisteNombre(){
        //given
        var nombre="nombreTest";
        //when
        boolean expected= cursoRepository.existsByNombre(nombre);
        //then
        assertThat(expected).isFalse();
    }
    @Test
    void debeEncontrarCursosPorNombre() {
        //given
        String nombreCurso = "Java";
        cursoRepository.save(curso);
        //when
        Pageable pageable = PageRequest.of(0, 3);
        Page<Curso> cursos= cursoRepository.search(nombreCurso, pageable);
        //then
        assertFalse(cursos.isEmpty());
        //verifica si todos los elementos del stream cumplen con el nombre pasado
        assertTrue(cursos.stream().allMatch(curso -> curso.getNombre().contains(nombreCurso)));
    }
    @Test
    void noDebeEncontrarCursosPorNombre() {
        //given
        var nombre="nombre";
        cursoRepository.save(curso);
        //when
        Pageable pageable = PageRequest.of(0, 3);
        Page<Curso> cursos= cursoRepository.search(nombre, pageable);
        //then
        assertTrue(cursos.isEmpty());
    }
}