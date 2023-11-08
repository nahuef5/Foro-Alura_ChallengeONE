package com.aluralatam.ForoAlura.controllers.curso;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.*;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.curso.services.CursoServiceImpl;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.QueryPageable;
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
    private QueryPageable buildQueryPageable(
            Integer pageNumber,
            Integer pageSize,
            String[]sortingParams
    )
    {
        return new QueryPageable(){
            @Override
            public Integer getPage() {
                return pageNumber;
            }
            @Override
            public Integer getElementByPage() {
                return pageSize;
            }
            @Override
            public String[] sortingParams() {
                return sortingParams;
            }};
    }
    @PostMapping("/create")
    public ResponseEntity<Response> save(@RequestBody @Valid CUCursoDto dto)
            throws EntityAlreadyExistsException
    {
        return cursoServiceImpl.save(dto);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<Response> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid CUCursoDto dto
    )throws EntityAlreadyExistsException, ResourceNotFoundException
    {
        return cursoServiceImpl.update(id, dto);
    }
    @PatchMapping("/active/{id}")
    public ResponseEntity<Response> active(@PathVariable("id")Long id)
            throws AccountActivationException, ResourceNotFoundException
    {
        return cursoServiceImpl.activate(id);
    }
    @DeleteMapping("/disable")
    public ResponseEntity<Response> disable(@RequestBody @Valid DeleteOrDesableCursoDto dto)
            throws BusinessRuleException, AccountActivationException, ResourceNotFoundException
    {
        return cursoServiceImpl.disable(dto);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Response>delete(@RequestBody @Valid DeleteOrDesableCursoDto dto)
            throws BusinessRuleException, ResourceNotFoundException
    {
        return cursoServiceImpl.delete(dto);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Curso>> getAll(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="nombre, asc",required=true)String[] sortingParams
    )throws EmptyEntityListException, BusinessRuleException
    {
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return cursoServiceImpl.findAllByPagination(queryPageable);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Curso>> getAllByNombre(
            @RequestBody @Valid NombreDto dto,
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="nombre, asc",required=true)String[] sortingParams
    )
        throws ResourceNotFoundException, BusinessRuleException
    {
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        var nombre=dto.nombre();
        return cursoServiceImpl.findAllByNombreAndPagination(nombre,queryPageable);
    }
    @GetMapping("/byId/{id}")
    public ResponseEntity<Curso>getById(
            @PathVariable("id") Long id
    )throws ResourceNotFoundException
    {
        return cursoServiceImpl.findById(id);
    }
    @GetMapping("/byNombreAndCategoria")
    public ResponseEntity<Curso>getByNombreAndCategoria(
            @RequestBody @Valid CUCursoDto dto)
            throws ResourceNotFoundException, BusinessRuleException {
        var nombre=dto.nombre();
        var categoria=dto.categoria();
        return cursoServiceImpl.findByNombreAndCategoria(nombre, categoria);
    }
}