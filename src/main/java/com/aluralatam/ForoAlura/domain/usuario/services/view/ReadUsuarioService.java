package com.aluralatam.ForoAlura.domain.usuario.services.view;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.Countries;
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
    private final String emptyField=Message.EMPTY_FIELD;
    public boolean isCountry(String pais){
        try{
            var replace=pais.toUpperCase().replace(" ","_");
            Countries.valueOf(replace);
            return true;
        }catch(IllegalArgumentException e){
            return false;
        }
    }
    public PageRequest buildPageRequest(String pageNumber, String pageSize, String[] sortingParams) {
        try {
            var pagNum = Integer.parseInt(pageNumber);
            var pagTam = Integer.parseInt(pageSize);
            if (pagNum < 1 || pagTam < 1)
                throw new IllegalArgumentException(Message.NUMBER_EXCEPTION);
            String field = sortingParams[0];
            String sortingDirection = sortingParams[1];
            Sort.Direction direction = sortingDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return PageRequest.of(pagNum -1,pagTam, Sort.by(direction,field));
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException(Message.NUMBER_EXCEPTION);
        }
    }
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
            String pageNumber,
            String pageSize,
            String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(nombreOApellido.isEmpty())
            throw new BusinessRuleException(emptyField);
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
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
            String pageNumber,
            String pageSize,
            String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(nombreOApellido.isEmpty())
            throw new BusinessRuleException(emptyField);
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
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
            String pageNumber,
            String pageSize,
            String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(nombreOApellido.isEmpty())
            throw new BusinessRuleException(emptyField);
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
        Page<Usuario>listaUsuarios=usuarioRepository
                .findAllInactiveUsersByNameOrSurname(nombreOApellido,pageRequest);
        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllUsersByActivo(@NotNull boolean activo,
                        String pageNumber,
                        String pageSize,
                        String[] sortingParams
    ) throws EmptyEntityListException {
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
        Page<Usuario>listaUsuarios=usuarioRepository.searchByActivo(activo,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new EmptyEntityListException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllUsersByCountry(String pais,
                         String pageNumber,
                         String pageSize,
                         String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(pais.isEmpty())
            throw new BusinessRuleException(emptyField);
        if(!isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);

        Page<Usuario>listaUsuarios=usuarioRepository.findAllByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllActiveUsersByCountry(String pais,
                               String pageNumber,
                               String pageSize,
                               String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(pais.isEmpty())
            throw new BusinessRuleException(emptyField);
        if(!isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllActiveUsersByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    @Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>>
        getAllInactiveUsersByCountry(String pais,
                                 String pageNumber,
                                 String pageSize,
                                 String[] sortingParams
    ) throws ResourceNotFoundException, BusinessRuleException {
        if(pais.isEmpty())
            throw new BusinessRuleException(emptyField);
        if(!isCountry(pais))
            throw new IllegalArgumentException("NO SE ENCUENTRA ESE PAIS EN NUESTRA LISTA.");
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllInactiveUsersByCountry(pais,pageRequest);

        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }
    /*@Transactional(readOnly=true)
    public ResponseEntity<List<Usuario>> getAllUsersToCommonUsers(
                String pageNumber,
                String pageSize,
                String[] sortingParams
    )
    {
        PageRequest pageRequest=buildPageRequest(pageNumber, pageSize, sortingParams);
        Page<Usuario>listaUsuarios=usuarioRepository.findAllAssets(pageRequest);
        List<Usuario>usuarios=listaUsuarios != null ? listaUsuarios.getContent() : new ArrayList<>();
        if(usuarios.isEmpty())
            throw new ResourceNotFoundException(Message.EMPTY_LIST);
        return ResponseEntity.status(HttpStatus.OK).body(usuarios);
    }*/
}