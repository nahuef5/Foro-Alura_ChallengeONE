package com.aluralatam.ForoAlura.domain.respuesta.service.repository;
import com.aluralatam.ForoAlura.domain.respuesta.model.entity.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface RespuestaRepository extends JpaRepository <Respuesta,Long>{

}