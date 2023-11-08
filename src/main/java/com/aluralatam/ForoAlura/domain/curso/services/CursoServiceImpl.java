package com.aluralatam.ForoAlura.domain.curso.services;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.repository.CursoRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class CursoServiceImpl implements CursoService {
    private final CursoRepository cursoRepository;
    @Transactional(rollbackFor = EntityAlreadyExistsException.class)
    @Override
    public ResponseEntity<Response> save(CUCursoDto dto) throws EntityAlreadyExistsException {
        var nombre = dto.nombre();
        var categoria = dto.categoria();
        if (cursoRepository.existsByNombreAndCategoria(nombre, categoria))
            throw new EntityAlreadyExistsException(Message.ALREADY_EXIST);
        Curso curso = new Curso(dto);
        cursoRepository.save(curso);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response(HttpStatus.CREATED, Message.CREATED));
    }
    @Transactional(rollbackFor = EntityAlreadyExistsException.class)
    @Override
    public ResponseEntity<Response> update(Long id, CUCursoDto dto) throws EntityAlreadyExistsException, ResourceNotFoundException {
        var nombre = dto.nombre();
        var categoria = dto.categoria();
        if (!cursoRepository.existsById(id))
            throw new ResourceNotFoundException(Message.NO_ID_EXISTS);
        if (cursoRepository.existsByNombreAndCategoria(nombre, categoria))
            throw new EntityAlreadyExistsException(Message.ALREADY_EXIST);
        Curso curso = cursoRepository.findById(id).get();
        curso.setNombre(nombre);
        curso.setCategoria(categoria);
        cursoRepository.save(curso);
        return ResponseEntity.ok()
                .body(new Response(HttpStatus.OK, Message.UPDATED));
    }
    @Transactional(rollbackFor = BusinessRuleException.class)
    @Override
    public ResponseEntity<Response> delete(DeleteOrDesableCursoDto dto) throws BusinessRuleException, ResourceNotFoundException {
        var id = dto.id();
        var confirm = dto.inactivo();
        if (!cursoRepository.existsById(id))
            throw new ResourceNotFoundException(Message.NO_ID_EXISTS);
        if (!confirm)
            throw new BusinessRuleException(Message.NOT_CONFIRMED);
        cursoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new Response(HttpStatus.ACCEPTED, Message.ELIMINATED));
    }
    @Transactional(rollbackFor = BusinessRuleException.class)
    @Override
    public ResponseEntity<Response> disable(DeleteOrDesableCursoDto dto) throws ResourceNotFoundException, BusinessRuleException, AccountActivationException {
        var id = dto.id();
        var inactivo = dto.inactivo();
        Curso curso = cursoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Message.NO_ID_EXISTS));
        if (curso.isInactivo() != false)
            throw new AccountActivationException(Message.RESOURCE_ALREADY_INACTIVED);
        if (!inactivo)
            throw new BusinessRuleException(Message.NOT_CONFIRMED);
        curso.setInactivo(true);
        cursoRepository.save(curso);
        ResponseEntity<Response> body=ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new Response(HttpStatus.ACCEPTED, Message.ELIMINATED));
        return body;
    }
    @Transactional(rollbackFor = AccountActivationException.class)
    @Override
    public ResponseEntity<Response> activate(Long id) throws AccountActivationException, ResourceNotFoundException {
        Curso curso = cursoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Message.NO_ID_EXISTS)
        );
        if (!curso.isInactivo())
            throw new AccountActivationException(Message.RESOURCE_ALREADY_ACTIVED);
        curso.setInactivo(false);
        cursoRepository.save(curso);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new Response(HttpStatus.ACCEPTED, Message.REACTIVATED_RESOURCE));
    }
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<Curso> findById(Long id) throws ResourceNotFoundException {
        Curso curso = cursoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Message.NO_ID_EXISTS));
        return ResponseEntity.status(HttpStatus.FOUND).body(curso);
    }
    @Transactional(readOnly=true)
    @Override
    public ResponseEntity<Curso> findByNombreAndCategoria(String nombre, String categoria) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidName = ChainChecker.invalidText(nombre);
        var isInvalidCategory = ChainChecker.invalidText(categoria);
        if(isInvalidName != null)
            throw new BusinessRuleException("Nombre: "+ isInvalidName);
        if(isInvalidCategory != null)
            throw new BusinessRuleException("Categoria: "+ isInvalidCategory);
        Curso curso=cursoRepository.findByNombreAndCategoria(nombre, categoria).orElseThrow(
                ()->new ResourceNotFoundException(Message.NO_PARAMETER_EXIST));
        return ResponseEntity.status(HttpStatus.FOUND).body(curso);
    }
    @Transactional(readOnly=true)
    @Override
    public ResponseEntity<List<Curso>> findAllByNombreAndPagination(
            String nombre,
            QueryPageable queryPageable
    )throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidName = ChainChecker.invalidText(nombre);
        if(isInvalidName != null)
            throw new BusinessRuleException("Nombre "+ isInvalidName);
        PageRequest pageRequest = PageRequestConstructor.buildPageRequest(queryPageable);
        if(!cursoRepository.existsByNombre(nombre))
            throw new ResourceNotFoundException(Message.NO_PARAMETER_EXIST);
        Page<Curso> cursoList = cursoRepository.search(nombre, pageRequest);

        List<Curso> cursos = cursoList.getContent()
                .stream()
                .filter(curso -> curso.isInactivo() == false)
                .collect(Collectors.toList());
        ResponseEntity<List<Curso>>body=ResponseEntity.status(HttpStatus.FOUND).body(cursos);
        return body;
    }
    //crear dos metodos para getAll
    @Transactional(readOnly=true)
    @Override
    public ResponseEntity<List<Curso>> findAllByPagination(
            QueryPageable queryPageable
    ) throws EmptyEntityListException, BusinessRuleException {
        PageRequest pageRequest = PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Curso> cursosPage = cursoRepository.findAll(pageRequest);
        List<Curso> cursos = cursosPage.getContent()
                .stream()
                .filter(curso -> curso.isInactivo() == false)
                .collect(Collectors.toList());
        if (cursos.isEmpty())
            throw new EmptyEntityListException(Message.EMPTY_LIST);

        ResponseEntity<List<Curso>> body=ResponseEntity.status(HttpStatus.FOUND).body(cursos);;
        return body;
    }
}