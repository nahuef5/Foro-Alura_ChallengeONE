package com.aluralatam.ForoAlura.domain.topico.service.repository;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.*;
@Repository
public interface TopicoRepository extends JpaRepository<Topico,Long>{
    /**
     * Encuentra los tópicos relacionados a un curso por el nombre del curso.
     * @param cursoNombre nombre del curso.
     * @param pageable Configuración de paginación.
     * @return página de tópicos relacionados al curso.
     */
    @Query(value=
            "SELECT t FROM Topico t "+
            "WHERE t.curso.nombre LIKE %:cursoNombre%")
    Page<Topico> findAllByCourse(@Param("cursoNombre")String cursoNombre, Pageable pageable);
    /**
     * Encuentra los tópicos relacionados a un usuario por su email.
     * @param email email del usuario.
     * @param pageable Configuración de paginación.
     * @return página de tópicos relacionados al usuario.
     */
    @Query(value=
            "SELECT t FROM Topico t " +
            "WHERE t.autor.email LIKE %:email%"
    )
    Page<Topico> findAllByUser(@Param("email") String email, Pageable pageable);
    /**
     * Encuentra los tópicos que contienen un título similar.
     * @param titulo título a buscar.
     * @param pageable Configuración de paginación.
     * @return página de tópicos con títulos similares.
     */
    @Query(value=
            "SELECT t FROM Topico t "+
            "WHERE t.titulo LIKE %:titulo%"
    )
    Page<Topico> findAllByTitle(@Param("titulo")String titulo, Pageable pageable);
    /**
     * Encuentra los tópicos por su status.
     * @param status status del tópico.
     * @param pageable Configuración de paginación.
     * @return página de tópicos con el estado proporcionado.
     */
    @Query(value=
            "SELECT t FROM Topico t "+
            "WHERE t.status=:status"
    )
    Page<Topico> findAllByStatus(@Param("status") StatusTopico status, Pageable pageable);
    Optional<Topico> findByTitulo(String titulo);
}