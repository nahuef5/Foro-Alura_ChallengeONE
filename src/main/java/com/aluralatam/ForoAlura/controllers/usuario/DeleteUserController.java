package com.aluralatam.ForoAlura.controllers.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.RemoveListaUsuariosDto;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.RemoveUsuarioDto;
import com.aluralatam.ForoAlura.domain.usuario.services.delete.DeleteUsuarioService;
import com.aluralatam.ForoAlura.global.exceptions.AccountActivationException;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import com.aluralatam.ForoAlura.global.tools.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class DeleteUserController {
    private final DeleteUsuarioService service;
    @DeleteMapping("/delete-user")
    public ResponseEntity<Response>deleteUserFromDDBB(@RequestBody RemoveUsuarioDto dto) throws BusinessRuleException, AccountActivationException, ResourceNotFoundException {
        return service.deleteUserFromDDBB(dto);
    }
    @DeleteMapping("/delete-users")
    public ResponseEntity<Response>deleteUsersFromDDBB(@RequestBody RemoveListaUsuariosDto dto) throws BusinessRuleException, AccountActivationException, ResourceNotFoundException {
        return service.deleteUsersFromDDBB(dto);
    }
}