package com.aluralatam.ForoAlura.controller;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.CursoServiceImpl;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.Response;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/curso")
@RequiredArgsConstructor
public class CursoController{
    private final CursoServiceImpl cursoServiceImpl;
    @PostMapping("/create")
    public ResponseEntity<Response> save(@RequestBody @Valid CUCursoDto dto) throws EntityAlreadyExistsException {
        return cursoServiceImpl.save(dto);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Response> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid CUCursoDto dto
    )throws EntityAlreadyExistsException, ResourceNotFoundException{
        return cursoServiceImpl.update(id, dto);
    }
    @PatchMapping("/active/{id}")
    public ResponseEntity<Response> active(@PathVariable("id")Long id)
            throws AccountActivationException, ResourceNotFoundException{
        return cursoServiceImpl.activate(id);
    }
    @DeleteMapping("/disable")
    public ResponseEntity<Response> disable(@RequestBody @Valid DeleteOrDesableCursoDto dto)
            throws BusinessRuleException, AccountActivationException, ResourceNotFoundException{
        return cursoServiceImpl.disable(dto);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Response>delete(@RequestBody @Valid DeleteOrDesableCursoDto dto)
            throws BusinessRuleException, ResourceNotFoundException{
        return cursoServiceImpl.delete(dto);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Curso>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = true) String pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "15", required = true) String pageSize
    ) throws EmptyEntityListException {
        return cursoServiceImpl.findAllByPagination(pageNumber, pageSize);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Curso>> getAllByNombre(
            @RequestBody @Valid NombreDto dto,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = true) String pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "15", required = true) String pageSize
    )
            throws ResourceNotFoundException{
        var nombre=dto.nombre();
        return cursoServiceImpl.findAllByNombreAndPagination(nombre,pageNumber,pageSize);
    }
    @GetMapping("/byId/{id}")
    public ResponseEntity<Curso>getById(
            @PathVariable("id") Long id
    )throws ResourceNotFoundException{

        return cursoServiceImpl.findById(id);
    }
    @GetMapping("/byNombreAndCategoria")
    public ResponseEntity<Curso>getByNombreAndCategoria(
            @RequestBody @Valid CUCursoDto dto) throws ResourceNotFoundException {
        var nombre=dto.nombre();
        var categoria=dto.categoria();
        return cursoServiceImpl.findByNombreAndCategoria(nombre, categoria);
    }
}