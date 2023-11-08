package com.aluralatam.ForoAlura.domain.topico;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.repository.TopicoRepository;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
public class TopicoRepositoryTest{
    @Autowired
    private UsuarioRepository uRepository;
    @Autowired
    private TopicoRepository tRepository;
    @Autowired
    private CursoRepository cRepository;
    private final String title="No entiendo Stream";
    private final StatusTopico status= StatusTopico.NO_RESPONDIDO;
    private Usuario user;
    private Topico topic;
    private Curso course;
    private final Pageable pageable = PageRequest.of(0, 15);
    @BeforeEach
    void setUp(){
        //Crear Curso
        Curso curso=Curso.builder()
                .id(1L)
                .nombre("Programacion Java Avanzado")
                .categoria("Programacion Funcional Parte I")
                .inactivo(false)
                .usuarios(new ArrayList<>())
                .topicos(new ArrayList<>())
                .build();
        course=cRepository.save(curso);
        //Crear usuario
        Usuario usuario=Usuario.builder()
                .id(1L)
                .dato(
                    DatoPersonal.builder()
                            .nombre("Nahuel")
                            .apellido("Funes")
                            .fechaNacimiento(LocalDate.of(1998,1,16))
                            .pais("Argentina")
                            .provincia("Córdoba")
                            .localidad("Capital")
                        .build())
                .email("nahuelffunes@email.com")
                .contrasena("Contra_12345")
                .cursos(new ArrayList<>())
                .topicos(new ArrayList<>())
                .respuestas(new ArrayList<>())
                .build();
        user =uRepository.save(usuario);
        //Agregar (setear) a lista de cursos de usuario
        user.addCursoToList(course);
        uRepository.save(user);
        //Crear topico
        Topico topico=new Topico(
                title,
                "Alguien me puede explicar sobre los flujos...",
                user,
                course

        );
        topic =tRepository.save(topico);
        //Agregar topico a usuario & curso
        user.addTopicoToList(topic);
        course.addTopicoToList(topic);
        cRepository.save(course);
        uRepository.save(user);
    }
    @AfterEach
    void tearDown(){
        tRepository.deleteAll();
        uRepository.deleteAll();
        cRepository.deleteAll();
    }
    @Test
    void itShouldBeAbleToFindTopicsByCourseName(){
        Page<Topico> topicos = tRepository.findAllByCourse("Programacion Java Avanzado",pageable);
        Topico actual=topicos.getContent().get(0);
        Topico expected=topic;
        assertEquals(1, topicos.getTotalElements());
        assertEquals(expected, actual);
    }
    @Test
    void itShouldBeAbleToFindTopicsByUserName(){
        Page<Topico>topicos=tRepository.findAllByUser("nahuelffunes@email.com",pageable);
        Topico actual=topicos.getContent().get(0);
        Topico expected=topic;
        assertEquals(1,topicos.getTotalElements());
        assertEquals(expected,actual);
    }
    @Test
    void itShouldBeAbleToFindTopicByTitle(){
        Page<Topico>topicos=tRepository.findAllByTitle(title,pageable);
        Topico actual=topicos.getContent().get(0);
        Topico expected=topic;
        assertEquals(1,topicos.getTotalElements());
        assertEquals(expected,actual);
    }
    @Test
    void itShouldBeAbleToFindTopicByStatus(){
        Page<Topico>topicos=tRepository.findAllByStatus(status,pageable);
        Topico actual=topicos.getContent().get(0);
        Topico expected=topic;
        assertEquals(1,topicos.getTotalElements());
        assertEquals(expected,actual);
    }
    @Test
    void itShouldBeAbleToGetOptionalTopicByTitle(){
        Optional<Topico>optionalTopic=tRepository.findByTitulo(title);
        boolean present=optionalTopic.isPresent();
        Topico expected=topic;
        assertTrue(present);
        assertEquals(expected, optionalTopic.get());
    }
}