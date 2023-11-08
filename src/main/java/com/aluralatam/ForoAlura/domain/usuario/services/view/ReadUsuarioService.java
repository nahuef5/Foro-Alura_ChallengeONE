package com.aluralatam.ForoAlura.domain.usuario.services.view;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RequiredArgsConstructor
@Service
public class ReadUsuarioService{
    private final UsuarioRepository usuarioRepository;
    private final String notFoundByID = Message.NO_ID_EXISTS;
    private final String badParameter =Message.NO_PARAMETER_EXIST;
    private final String errorMessage ="Error de validacion: ";

    @Transactional(readOnly=true)
    public ResponseEntity<Usuario> getUserById(Long id) throws ResourceNotFoundException {
        Usuario usuario=usuarioRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException(notFoundByID)
        );
        return ResponseEntity.ok().body(usuario);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<Usuario>getUserByEmail(String email) throws ResourceNotFoundException {
        Usuario usuario=usuarioRepository.findByEmail(email).orElseThrow(
                ()->new ResourceNotFoundException(badParameter)
        );
        return ResponseEntity.ok().body(usuario);
    }
    //GET MANY
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>getAllUsersByNameOrSurname(
            String nombreOApellido,
            QueryPageable queryPageable
    ) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(nombreOApellido);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllUsersByNameOrSurname(nombreOApellido,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllActiveUsersByNameOrSurname(
            String nombreOApellido,
            QueryPageable queryPageable
    ) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(nombreOApellido);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository
                .findAllActiveUsersByNameOrSurname(nombreOApellido,pageRequest);
        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllInactiveUsersByNameOrSurname(
            String nombreOApellido,
            QueryPageable queryPageable
    ) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(nombreOApellido);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository
                .findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest);
        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllUsersByActivo(
                @NotNull boolean activo,
                QueryPageable queryPageable
    ) throws EmptyEntityListException, BusinessRuleException
    {
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository.searchByActivo(activo,pageRequest);
        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new EmptyEntityListException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllUsersByCountry(
                String pais,
                QueryPageable queryPageable
    ) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(pais);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        if(!ChainChecker.isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllActiveUsersByCountry(
                String pais,
                QueryPageable queryPageable
    ) throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(pais);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        if(!ChainChecker.isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllActiveUsersByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllInactiveUsersByCountry(
                String pais,
                QueryPageable queryPageable
    )throws ResourceNotFoundException, BusinessRuleException {
        var isInvalidText = ChainChecker.invalidText(pais);
        if(isInvalidText != null)
            throw new BusinessRuleException(errorMessage+ isInvalidText);
        if(!ChainChecker.isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=PageRequestConstructor.buildPageRequest(queryPageable);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllInactiveUsersByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
}