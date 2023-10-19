package com.aluralatam.ForoAlura.domain.usuario.services.repository;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    Optional<Usuario>findByEmail(String email);
    boolean existsByEmail(String email);
    /**
    * Verifica si existe un usuario con ese nombre
    *
    * @param nombre nombre a verificar
    * @return true si existe un usuario con ese nombre  pero false si no
    */
    @Query(value=
            "SELECT CASE WHEN COUNT(u) > 0 "
            +"THEN TRUE ELSE FALSE END "
            +"FROM Usuario u "
            +"WHERE u.dato.nombre=:nombre"
    )
    boolean existsByNombre(@Param("nombre") String nombre);
    /**
    * Verifica si existe un usuario con ese apellido
    *
    * @param apellido  a verificar
    * @return true si existe ese apellido, false si no
    */
    @Query(value =
            "SELECT CASE WHEN COUNT(u) > 0 "
            +"THEN TRUE ELSE FALSE END "
            +"FROM Usuario u "
            +"WHERE u.dato.apellido=:apellido"
    )
    boolean existsByApellido(@Param("apellido") String apellido);

    /**
    * Setea la activación a false a un usuario
    *
    * @param id id del usuario al que se le cambiará el estado de activación
    *
    */
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
            value = "UPDATE Usuario  u SET  u.activo = false WHERE u.id = ?1"
    )
    void setInactiveToUser(Long id);
    /**
     * Setea la activación a una lista de usuarios
     *
     * @param listaUsuarios lista de usuarios a los que se les
     *                     cambiará el estado de activación
     */
    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value =
            "UPDATE Usuario u "
                    + "SET u.activo = false "
                    + "WHERE u IN ?1"
    )
    void setInactiveToUserList(List<Usuario> listaUsuarios);

    /**
    * Busca usuarios activos por nombre o apellido.
    *
    * @param nombreOApellido nombre o apellido a buscar
    * @param pageable  información sobre la paginación
    * @return una página de usuarios activos que coincide con el nombre o apellido
    */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.activo=true "
            +"AND (u.dato.nombre LIKE %:nombreOApellido% "
            +"OR u.dato.apellido LIKE %:nombreOApellido%)"
    )
    Page<Usuario> findAllActiveUsersByNameOrSurname(
            @Param("nombreOApellido") String nombreOApellido, Pageable pageable);

    /**
     * Busca usuarios por nombre o apellido
     *
     * @param nombreOApellido nombre o apellido a buscar
     * @param pageable información sobre la paginación
     * @return una página de usuarios activos que coincide con el nombre o apellido
     *
     */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.dato.nombre LIKE %:nombreOApellido% "
            +"OR u.dato.apellido LIKE %:nombreOApellido%"
    )
    Page<Usuario> findAllUsersByNameOrSurname(
            @Param("nombreOApellido") String nombreOApellido, Pageable pageable);
    /**
    * Busca usuarios inactivos por nombre o apellido
    *
    * @param nombreOApellido nombre o apellido a buscar
    * @param pageable  información sobre la paginación
    * @return una página de usuarios inactivos que coincide con el nombre o apellido
    *
    */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.activo=false "
            +"AND (u.dato.nombre LIKE %:nombreOApellido% "
            +"OR u.dato.apellido LIKE %:nombreOApellido%)"
    )
    Page<Usuario>findAllInactiveUsersByNameOrSurname(
            @Param("nombreOApellido") String nombreOApellido, Pageable pageable);
    /**
    * Busca usuarios activos por país
    *
    * @param pais  país a buscar.
    * @param pageable  información sobre la paginación
    * @return una página de usuarios activos que coincide con el país
    *
    */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.activo=true "
            +"AND u.dato.pais LIKE %:pais%"
    )
    Page<Usuario>findAllActiveUsersByCountry(
            @Param("pais") String pais, Pageable pageable);
    /**
    * Busca usuarios por pais
    *
    * @param pais   país a buscar.
    * @param pageable  información sobre la paginación.
    * @return una página de usuarios inactivos que coincide con el país
    *
    */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.activo=false "
            +"AND u.dato.pais LIKE %:pais%"
    )
    Page<Usuario>findAllInactiveUsersByCountry(
            @Param("pais") String pais, Pageable pageable);
    /**
     * Busca usuarios por país
     *
     * @param pais  país a buscar
     * @param pageable  proporciona informacion sobre la paginacion
     * @return una pagina de usuarios que coincide con el país
     */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.dato.pais LIKE %:pais%"
    )
    Page<Usuario>findAllByCountry(
            @Param("pais") String pais, Pageable pageable);
    /**
    * Busca usuarios por activación
    *
    * @param activo  activación a buscar
    * @param pageable  proporciona información sobre la paginación
    * @return una página de usuarios con la activación establecida
    */
    @Query(value =
            "SELECT u FROM Usuario u "
                    +"WHERE u.activo = :activo"
    )
    Page<Usuario>searchByActivo(boolean activo, Pageable pageable);
    /**
    * Busca usuarios activos
    *
    * @param pageable  proporciona información de la paginación
    * @return una página de usuarios activos
    */
    @Query(value =
            "SELECT u FROM Usuario u "
            +"WHERE u.activo= true"
    )
    Page<Usuario>findAllAssets(Pageable pageable);
}