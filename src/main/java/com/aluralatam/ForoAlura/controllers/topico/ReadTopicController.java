package com.aluralatam.ForoAlura.controllers.topico;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.topico.service.view.ReadTopicoService;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.ByParameterDto;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/topico")
@RequiredArgsConstructor
public class ReadTopicController {
    private final ReadTopicoService service;
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
    public ResponseEntity<Topico> getById(@PathVariable("id")Long id)
            throws ResourceNotFoundException
    {
        return service.getOneById(id);
    }
    @GetMapping("/getByTitle/{titulo}")
    public ResponseEntity<Topico> getByTitle(@PathVariable("titulo")@Valid String title)
            throws ResourceNotFoundException, BusinessRuleException
    {
        return service.getOneByTitle(title);
    }
    @GetMapping("/getAllByTitulo")
    public ResponseEntity<List<Topico>>getAllByTitle(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="titulo, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException{
        final String title=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllByTitle(
                title,
                queryPageable
        );
    }
    @GetMapping("/getAllByCourse")
    public ResponseEntity<List<Topico>>getAllByCurso(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="titulo, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException{
        final String curso=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllByCourseName(
                curso,
                queryPageable
        );
    }
    @GetMapping("/getAllByUser/{email}")
    public ResponseEntity<List<Topico>>getAllByUsuario(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="titulo, asc",required=true)String[] sortingParams,
            @PathVariable("email") String email
    ) throws BusinessRuleException, ResourceNotFoundException{

        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllByEmail(
                email,
                queryPageable
        );
    }
    @GetMapping("/getAllByStatusTopico")
    public ResponseEntity<List<Topico>>getAllByStatus(
            @RequestParam
                    (value="pageNumber", defaultValue="0", required=true) Integer pageNumber,
            @RequestParam
                    (value="pageSize", defaultValue="10", required=true) Integer pageSize,
            @RequestParam
                    (value="sort",defaultValue="titulo, asc",required=true)String[] sortingParams,
            @RequestBody @Valid ByParameterDto dto
    ) throws BusinessRuleException, ResourceNotFoundException{
        final String status=dto.parameter();
        QueryPageable queryPageable=buildQueryPageable(pageNumber,pageSize,sortingParams);
        return service.getAllByStatus(
                status,
                queryPageable
        );
    }
}