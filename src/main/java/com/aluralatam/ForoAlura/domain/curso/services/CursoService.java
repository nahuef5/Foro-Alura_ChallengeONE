package com.aluralatam.ForoAlura.domain.curso.services;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.Response;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
public interface CursoService{
    public ResponseEntity<Response> save(CUCursoDto dto)
            throws EntityAlreadyExistsException;
    public ResponseEntity<Response> update(Long id, CUCursoDto dto)
            throws ResourceNotFoundException,EntityAlreadyExistsException;
    public ResponseEntity<Response> delete(DeleteOrDesableCursoDto dto)
            throws ResourceNotFoundException, NotConfirmedException;
    public ResponseEntity<Response> disable(DeleteOrDesableCursoDto dto)
            throws ResourceNotFoundException, AccountActivationException;
    public ResponseEntity<Response> activate(Long id)
            throws ResourceNotFoundException, AccountActivationException;
    public ResponseEntity<Curso> findById(Long id)throws ResourceNotFoundException;
    public ResponseEntity<Curso> findByNombreAndCategoria(String nombre, String categoria)
            throws ResourceNotFoundException;
    public ResponseEntity<Page<Curso>> findAllByNombreAndPagination(String nombre, Pageable pageable)
            throws ResourceNotFoundException, EmptyEntityListException;
    public ResponseEntity<Page<Curso>> findAllByPagination(Pageable pageable)
            throws EmptyEntityListException;
}