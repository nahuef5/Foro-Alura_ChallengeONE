package com.aluralatam.ForoAlura.domain.curso.services;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.QueryPageable;
import com.aluralatam.ForoAlura.global.tools.Response;
import org.springframework.http.ResponseEntity;
import java.util.List;
public interface CursoService{
    public ResponseEntity<Response> save(CUCursoDto dto)
            throws EntityAlreadyExistsException;
    public ResponseEntity<Response> update(Long id, CUCursoDto dto)
            throws ResourceNotFoundException, EntityAlreadyExistsException;
    public ResponseEntity<Response> delete(DeleteOrDesableCursoDto dto) throws BusinessRuleException, ResourceNotFoundException;
    public ResponseEntity<Response> disable(DeleteOrDesableCursoDto dto)
            throws ResourceNotFoundException, BusinessRuleException, AccountActivationException;
    public ResponseEntity<Response> activate(Long id) throws AccountActivationException, ResourceNotFoundException;
    public ResponseEntity<Curso> findById(Long id) throws ResourceNotFoundException;
    public ResponseEntity<Curso> findByNombreAndCategoria(String nombre, String categoria) throws ResourceNotFoundException, BusinessRuleException;
    public ResponseEntity<List<Curso>> findAllByNombreAndPagination(String nombre, QueryPageable queryPageable) throws ResourceNotFoundException, BusinessRuleException;
    public ResponseEntity<List<Curso>> findAllByPagination(QueryPageable queryPageable) throws EmptyEntityListException, BusinessRuleException;
}