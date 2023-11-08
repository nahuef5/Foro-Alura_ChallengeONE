package com.aluralatam.ForoAlura.controllers.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.view.ReadUsuarioService;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.*;
import org.springframework.http.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class ReadUserController {
    private final ReadUsuarioService service;
    private QueryPageable buildQueryPageable(
            Integer pageNumber,
            Integer pageSize,
            String[]sortingParams){
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
    @GetMapping("/getById/{id}")
    public ResponseEntity<Usuario>getById(@PathVariable("id")Long id) throws ResourceNotFoundException {
        return service.getUserById(id);
    }
    @GetMapping("/getByEmail")
    public ResponseEntity<Usuario>getByEmail(@RequestBody @Valid ByParameterDto dto) throws ResourceNotFoundException {
        final String email=dto.parameter();
        return service.getUserByEmail(email);
    }
    @GetMapping("/getAllByNoS")
    public ResponseEntity<List<Usuario>>getAllByNameOrSurname(
        @RequestParam
            (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
        @RequestParam
            (value="pageSize", defaultValue="10", required=true) Integer pageSize,
        @RequestParam
            (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
        @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException{
        final String nombreOApellido=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllUsersByNameOrSurname(
                nombreOApellido,
                queryPageable
                );
    }
    @GetMapping("/getAllActiveByNoS")
    public ResponseEntity<List<Usuario>>getAllActiveByNameOrSurname(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String nombreOApellido=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllActiveUsersByNameOrSurname(
                nombreOApellido,
                queryPageable
        );
    }
    @GetMapping("/getAllInactiveByNoS")
    public ResponseEntity<List<Usuario>>getAllInactiveByNameOrSurname(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String nombreOApellido=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllInactiveUsersByNameOrSurname(
                nombreOApellido,
                queryPageable
                );
    }
    @GetMapping("/getAllByActivo/{activo}")
    public ResponseEntity<List<Usuario>>getAllByActivo(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @PathVariable("activo") boolean activo
    ) throws EmptyEntityListException, BusinessRuleException
    {
        QueryPageable queryPageable=buildQueryPageable(
                pageNumber,
                pageSize,
                sortingParams
        );
        return service.getAllUsersByActivo(
                activo,
                queryPageable
        );
    }
    @GetMapping("/getAllByCountry")
    public ResponseEntity<List<Usuario>>getAllByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(
                pageNumber,
                pageSize,
                sortingParams
        );
        return service.getAllUsersByCountry(pais,queryPageable);
    }
    @GetMapping("/getAllActiveByCountry")
    public ResponseEntity<List<Usuario>>getAllActiveByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(
                pageNumber,
                pageSize,
                sortingParams
        );
        return service.getAllActiveUsersByCountry(pais,queryPageable);
    }
    @GetMapping("/getAllInactiveByCountry")
    public ResponseEntity<List<Usuario>>getAllInactiveByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(
                pageNumber,
                pageSize,
                sortingParams
        );
        return service.getAllInactiveUsersByCountry(pais,queryPageable);
    }
}