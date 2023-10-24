package com.aluralatam.ForoAlura.domain.usuario.services.delete;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.services.repository.UsuarioRepository;
import com.aluralatam.ForoAlura.global.exceptions.*;
import com.aluralatam.ForoAlura.global.tools.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class DeleteUsuarioService{
    private final UsuarioRepository usuarioRepository;
    private final Response responseEliminated=Response.builder()
            .httpStatus(HttpStatus.ACCEPTED)
            .respuesta(Message.ELIMINATED)
            .build();
    private final String notFoundByID =Message.NO_ID_EXISTS;
    private final String notConfirmed=Message.NOT_CONFIRMED;
    private final String alreadyActivated =Message.RESOURCE_ALREADY_ACTIVED;
    /**
     * Verifica si todos los ids de usuarios proporcionados existen en ddbb.
     *
     * @param ids lista de los ids de usuarios.
     * @return true si todos los ids existen, false de lo contrario.
     */
    private boolean existsAllUsers(List<Long>ids){
        final List<Long>existingIds=usuarioRepository.findAllById(ids)
                .stream()
                .map(Usuario::getId)
                .toList();
        return new HashSet<>(existingIds).containsAll(ids);
    }
    /**
     * Obtiene una lista de ids que no existen en la base de datos
     *
     * @param ids lista de los supuestos ids de usuarios
     * @return lista de ids que no existen en la ddbbb.
     */
    private List<Long> getNonexistentIds(List<Long>ids){
        final List<Long>existingIds=usuarioRepository.findAllById(ids)
                .stream().map(Usuario::getId)
                .toList()
                ;
        return ids
                .stream()
                .filter(id->!existingIds.contains(id))
                .collect(Collectors.toList());
    }
    /**
     * Retorna un string que contiene los ids de usuarios que no existen en la ddbb.
     * y los elementos están separados por comas.
     *
     * @param ids lista de los supuestos ids de usuarios.
     * @return string que contiene los ids de usuarios inexistentes.
     */
    private String printNonExistingIds(List<Long>ids){
        final List<Long>idsAsString=getNonexistentIds(ids);
        return idsAsString
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(",","",""));
    }
    /**
     * Verifica si todos los usuarios con los ids proporcionados están inactivos.
     *
     * @param ids lista de ids de usuarios a verificar.
     * @return true si todos los usuarios están inactivos, false si al menos uno no lo está.
     */
    private boolean areAllInactive(List<Long>ids){
        return usuarioRepository.findAllById(ids)
                .stream()
                .allMatch(
                        u->!u.isActivo()
                );
    }
    /**
     * Trae una lista de usuarios activos a partir de una lista de ids.
     *
     * @param ids lista de ids de usuarios donde se obtienen los usuarios activos.
     * @return lista de instancias de usuarios que están activos.
     */
    private List<Usuario> getAllActives(List<Long>ids){
        return usuarioRepository.findAllById(ids)
                .stream().filter(u-> u.isActivo())
                .collect(Collectors.toList());
    }
    /**
     * Retorna un string que contiene los ids de usuarios activos junto con un mensaje
     * los elementos estan separados por comas
     *
     * @param ids lista de los ids de usuarios
     * @return string que contiene los ids de usuarios activos con mensajes
     */
    private String printAllActives(List<Long>ids){
        final List<Usuario>usuariosInactivos=getAllActives(ids);
        return usuariosInactivos
                .stream()
                .map(usuario -> usuario.getId()+" "+alreadyActivated+",")
                .collect(Collectors.joining(" "));
    }
    /**
     * Elimina una cuenta de usuario de la ddbb.
     *
     * @param dto objeto para eliminar la cuenta.
     * @return Respuesta de éxito con el estado HTTP ACCEPTED.
     * @throws ResourceNotFoundException si el id del usuario no existe en la ddbb.
     * @throws AccountActivationException si el usuario se encuentra activo.
     * @throws BusinessRuleException si la eliminación no está confirmada.
     */
    @Validated
    @Transactional(rollbackFor={
            AccountActivationException.class,
            BusinessRuleException.class
    })
    public ResponseEntity<Response> deleteUserFromDDBB(@Valid RemoveUsuarioDto dto) throws AccountActivationException, BusinessRuleException, ResourceNotFoundException {
        final Long id=dto.id();
        Usuario usuario=usuarioRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException(notFoundByID)
        );
        if (!dto.remove())
            throw new BusinessRuleException(notConfirmed);
        if(usuario.isActivo())
            throw new AccountActivationException(alreadyActivated);
        usuarioRepository.delete(usuario);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseEliminated);
    }
    /**
     * Elimina una lista de cuentas de usuario en lote de la ddbb.
     *
     * @param dto objeto para eliminar las cuentas.
     * @return Respuesta de éxito con el estado HTTP ACCEPTED.
     * @throws ResourceNotFoundException si algun id de usuario no existe en la ddbb.
     * @throws AccountActivationException si se encuentran usuarios activos.
     * @throws BusinessRuleException si la eliminación no está confirmada.
     */
    @Validated
    @Transactional(rollbackFor = {
            ResourceNotFoundException.class,
            AccountActivationException.class,
            BusinessRuleException.class
    })
    public ResponseEntity<Response>deleteUsersFromDDBB(@Valid RemoveListaUsuariosDto dto) throws AccountActivationException, BusinessRuleException, ResourceNotFoundException {
        final List<Long> ids=dto.ids();
        String errorList = Message.RESOURCES_DO_NOT_EXIST;
        if(!existsAllUsers(ids))
            throw new ResourceNotFoundException(errorList +printNonExistingIds(ids));
        if(!dto.remove())
            throw new BusinessRuleException(notConfirmed);
        if(!areAllInactive(ids))
            throw new AccountActivationException(printAllActives(ids));
        usuarioRepository.deleteAllByIdInBatch(ids);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseEliminated);
    }
}