package com.aluralatam.ForoAlura.domain.curso.services.repository;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{
    /**
     * Busca cursos por nombre con paginación.
     * @param nombre nombre del curso a buscar.
     * @param pageable Configuración de paginación.
     * @return página de cursos con el nombre indicado.
     */
    @Query(value =
            "SELECT c FROM Curso c "
                    +"WHERE c.nombre LIKE %:nombre% ")
    Page<Curso> search(@Param("nombre") String nombre, Pageable pageable);
    Optional<Curso> findByNombreAndCategoria(String nombre, String categoria);
    Optional<Curso> findByNombre(String nombre);

    @Query("SELECT CASE WHEN COUNT(c) > 0 "
            + "THEN true "
            + "ELSE false END "
            + "FROM Curso c "
            + "WHERE c.nombre = :nombre "
            + "AND c.categoria = :categoria")
    boolean existsByNombreAndCategoria(@Param("nombre") String nombre, @Param("categoria") String categoria);
    @Query(
            "SELECT CASE WHEN COUNT(c) > 0 "
                    +"THEN TRUE ELSE FALSE END "
                    + "FROM Curso c "
                    + "WHERE c.nombre = :nombre"
    )
    boolean existsByNombre(@Param("nombre")String nombre);
    /**
     * Query para obtener cursos.
     * Cada array contiene los campos seleccionados en orden:
     * nombre y categoria.
     *
     * @return List<Object[]> lista de arrays de objs,
     * cada array contiene los campos indicados.
     */
    @Query(value=
            "SELECT  c.nombre, " +
                    "c.categoria " +
                    "FROM Curso c"
    )
    List<Object[]> findAllCourses();
}