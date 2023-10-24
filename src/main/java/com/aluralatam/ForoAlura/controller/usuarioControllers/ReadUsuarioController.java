package com.aluralatam.ForoAlura.controller.usuarioControllers;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.view.ReadUsuarioService;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import com.aluralatam.ForoAlura.global.exceptions.EmptyEntityListException;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import lombok.*;
import org.springframework.http.*;
import java.util.*;
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class ReadUsuarioController {
    private final ReadUsuarioService service;
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
            (value="pageNumber", defaultValue="0", required=true) String pageNumber,
        @RequestParam
            (value="pageSize", defaultValue="10", required=true) String pageSize,
        @RequestParam
            (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
        @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String nombreOApellido=dto.parameter();
        return service.getAllUsersByNameOrSurname(
                nombreOApellido,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllActiveByNoS")
    public ResponseEntity<List<Usuario>>getAllActiveByNameOrSurname(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String nombreOApellido=dto.parameter();
        return service.getAllActiveUsersByNameOrSurname(
                nombreOApellido,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllInactiveByNoS")
    public ResponseEntity<List<Usuario>>getAllInactiveByNameOrSurname(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String nombreOApellido=dto.parameter();
        return service.getAllInactiveUsersByNameOrSurname(
                nombreOApellido,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllByActivo/{activo}")
    public ResponseEntity<List<Usuario>>getAllByActivo(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @PathVariable("activo") boolean activo
    ) throws EmptyEntityListException {
        return service.getAllUsersByActivo(
                activo,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllByCountry")
    public ResponseEntity<List<Usuario>>getAllByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        return service.getAllUsersByCountry(
                pais,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllActiveByCountry")
    public ResponseEntity<List<Usuario>>getAllActiveByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        return service.getAllActiveUsersByCountry(
                pais,
                pageNumber,
                pageSize,
                sortingParams);
    }
    @GetMapping("/getAllInactiveByCountry")
    public ResponseEntity<List<Usuario>>getAllInactiveByCountry(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) String pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) String pageSize,
            @RequestParam
                    (value="sort",defaultValue="dato.apellido, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException {
        final String pais=dto.parameter();
        return service.getAllInactiveUsersByCountry(
                pais,
                pageNumber,
                pageSize,
                sortingParams);
    }
}