package com.aluralatam.ForoAlura.domain.usuario.services.create;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateUsuarioDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
@RequiredArgsConstructor
@Service
public class CreateUsuarioService{
    private final UsuarioRepository usuarioRepository;
    private final Response response = Response.builder()
            .httpStatus(HttpStatus.CREATED)
            .respuesta(Message.CREATED)
            .build();
    private final String errorMessage=Message.ALREADY_EXIST;
    private final String errorPassword=Message.PASSWORDS_DO_NOT_MATCH;
    private boolean userAlreadyExists(String email){
        return usuarioRepository.existsByEmail(email);
    }
    private boolean differentPassword(String password, String password2){return !password.equals(password2);}
    private void saveUser(CreateUsuarioDTO dto){
        Usuario usuario=new Usuario(dto);
        usuarioRepository.saveAndFlush(usuario);
    }
    @Validated
    @Transactional(rollbackFor = {
            EntityAlreadyExistsException.class,
            BusinessRuleException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Response> registerNewCommonUser(@Valid CreateUsuarioDTO dto) throws EntityAlreadyExistsException, BusinessRuleException {
        final String email=dto.email();
        final String clave1=dto.contrasena();
        final String clave2=dto.confirmarContrasena();
        final String pais=dto.datosPersonales().pais();
        if(differentPassword(clave1,clave2))
            throw new BusinessRuleException(errorPassword);
        if(!ChainChecker.isCountry(pais))
            throw new BusinessRuleException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        if(userAlreadyExists(email))
            throw new EntityAlreadyExistsException(errorMessage);
        saveUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}