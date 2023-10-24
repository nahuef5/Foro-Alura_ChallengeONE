package com.aluralatam.ForoAlura.controller.usuarioControllers;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateUsuarioDTO;
import com.aluralatam.ForoAlura.domain.usuario.services.create.CreateUsuarioService;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import com.aluralatam.ForoAlura.global.exceptions.EntityAlreadyExistsException;
import com.aluralatam.ForoAlura.global.tools.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class CreateUsuarioController{
    private final CreateUsuarioService service;
    @PostMapping("/register")
    public ResponseEntity<Response> createCommonUser(@RequestBody CreateUsuarioDTO dto) throws BusinessRuleException, EntityAlreadyExistsException {
        return service.registerNewCommonUser(dto);
    }
}