package com.aluralatam.ForoAlura.controllers.usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.services.modifications.ModifyUsuarioService;
import com.aluralatam.ForoAlura.global.exceptions.AccountActivationException;
import com.aluralatam.ForoAlura.global.exceptions.BusinessRuleException;
import com.aluralatam.ForoAlura.global.exceptions.ResourceNotFoundException;
import com.aluralatam.ForoAlura.global.tools.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/usuario")
@RequiredArgsConstructor
public class ModifyUserController {
    private final ModifyUsuarioService service;
    @PutMapping("/update-data/{id}")
    public ResponseEntity<Response> updateByPersonalInformation(
            @PathVariable("id")Long id,
            @RequestBody UpdateDatoPersonalDTO dto
    ) throws ResourceNotFoundException {
        return service.updateUserByPersonalInformation(id,dto);
    }
    @PatchMapping("/new-password")
    public ResponseEntity<Response>generateANewPassword(@RequestBody NuevaContrasena dto) throws BusinessRuleException, ResourceNotFoundException, AccountActivationException {
        return service.generateANewPassword(dto);
    }
    @PatchMapping("/disable-user")
    public ResponseEntity<Response>disableAccount(@RequestBody RemoveUsuarioDto dto) throws BusinessRuleException, ResourceNotFoundException, AccountActivationException {
        return service.disableAccount(dto);
    }
    @PatchMapping("/disable-users")
    public ResponseEntity<Response>disableAccounts(@RequestBody RemoveListaUsuariosDto dto) throws BusinessRuleException, ResourceNotFoundException, AccountActivationException {
        return service.disableAccounts(dto);
    }
    @PatchMapping("/reactive-users")
    public ResponseEntity<Response>reactivateAccountByEmail(
            @RequestBody @Valid ByParameterDto emailDto) throws ResourceNotFoundException, AccountActivationException {
        final String email=emailDto.parameter();
        return service.reactivateAccountByEmail(email);
    }
}