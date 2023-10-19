package com.aluralatam.ForoAlura.domain.curso.services.repository;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{
    @Query(value =
            "SELECT c FROM Curso c "
            +"WHERE c.nombre LIKE %:nombre% ")
    Page<Curso> search(@Param("nombre") String nombre, Pageable pageable);
    Optional<Curso> findByNombreAndCategoria(String nombre, String categoria);

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
}