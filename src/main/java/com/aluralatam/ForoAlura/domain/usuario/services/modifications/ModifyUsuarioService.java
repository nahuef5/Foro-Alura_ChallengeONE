package com.aluralatam.ForoAlura.domain.usuario.services.modifications;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.*;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
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
public class ModifyUsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final Response responseOK = Response.builder()
            .httpStatus(HttpStatus.OK)
            .respuesta(Message.UPDATED)
            .build();
    private final Response responseAccepted=Response.builder()
            .httpStatus(HttpStatus.ACCEPTED)
            .respuesta(Message.REACTIVATED_RESOURCE)
            .build();
    private final Response responseEliminated=Response.builder()
            .httpStatus(HttpStatus.ACCEPTED)
            .respuesta(Message.ELIMINATED)
            .build();
    private final String alreadyActivated=Message.RESOURCE_ALREADY_ACTIVED;
    private final String notConfirmed=Message.NOT_CONFIRMED;
    private final String notFoundByID =Message.NO_ID_EXISTS;
    private final String notFoundByEmail =Message.NO_PARAMETER_EXIST;
    private final String alreadyInactivated =Message.RESOURCE_ALREADY_INACTIVED;
    private final String errorPassword=Message.PASSWORDS_DO_NOT_MATCH;

    /**
     * Compara dos claves para verificar si son diferentes.
     *
     * @param password primera clave a comparar.
     * @param password2 segunda clave a comparar.
     * @return true si las claves son diferentes, false si son iguales.
     */
    private boolean differentPassword(String password,String password2){return !password.equals(password2);}
    /**
     * Verifica si un usuario dado está activo.
     *
     * @param usuario instancia del usuario a verificar.
     * @return true si el usuario está activo, false si no lo está.
     */
    private boolean isAnActiveUser(Usuario usuario){return usuario.isActivo();}
    /**
     * Verifica si todos los usuarios con los ids proporcionados están activos.
     *
     * @param ids lista de ids de usuarios a verificar.
     * @return true si todos los usuarios están activos, false si al menos uno no lo está.
     */
    private boolean areAllActive(List<Long>ids){
        return usuarioRepository.findAllById(ids)
                .stream()
                .allMatch(
                        Usuario::isActivo
                );
    }
    /**
     * Trae una lista de usuarios inactivos a partir de una lista de ids.
     *
     * @param ids lista de ids de usuarios donde se obtienen los usuarios inactivos.
     * @return lista de instancias de usuarios que están inactivos.
     */
    private List<Usuario> getAllInactives(List<Long>ids){
        return usuarioRepository.findAllById(ids)
                .stream().filter(u-> !u.isActivo())
                .collect(Collectors.toList());
    }
    /**
     * Retorna un string que contiene los ids de usuarios inactivos junto con un mensaje
     * los elementos estan separados por comas
     *
     * @param ids lista de los ids de usuarios
     * @return string que contiene los ids de usuarios inactivos con mensajes
     */
    public String printAllInactives(List<Long>ids){
        final List<Usuario>usuariosInactivos=getAllInactives(ids);
        return usuariosInactivos
                .stream()
                .map(usuario -> usuario.getId()+" "+alreadyInactivated+",")
                .collect(Collectors.joining(" "));
    }
    /**
     * Verifica si todos los ids de usuarios proporcionados existen en ddbb.
     *
     * @param ids lista de los ids de usuarios.
     * @return true si todos los ids existen, false de lo contrario.
     */
    private boolean existsAllUsers(List<Long>ids){
        final List<Long>existingIds=usuarioRepository.findAllById(ids)
                .stream().map(Usuario::getId)
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
                .collect(Collectors.joining(",","",""))
        ;
    }
    /**
     * Actualiza los datos personales de un usuario.
     *
     * @param id  id del usuario.
     * @param dto objeto para la actualización.
     * @return Respuesta de éxito con el estado HTTP OK.
     * @throws ResourceNotFoundException si el id del usuario no existe en la ddbb.
     */
    @Validated
    @Transactional
    public ResponseEntity<Response> updateUserByPersonalInformation
            (Long id, @Valid UpdateDatoPersonalDTO dto) throws ResourceNotFoundException {
        Usuario usuario=usuarioRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException(notFoundByID));

        DatoPersonal datoPersonal=usuario.getDato();
        datoPersonal.setNombre(dto.nombre());
        datoPersonal.setApellido(dto.apellido());
        datoPersonal.setProvincia(dto.provincia());
        datoPersonal.setLocalidad(dto.localidad());

        usuario.setDato(datoPersonal);
        usuarioRepository.saveAndFlush(usuario);
        return ResponseEntity.ok().body(responseOK);
    }
    /**
     * Modifica una nueva clave para un usuario.
     *
     * @param dto objeto para generar la nueva clave.
     * @return Respuesta de éxito con el estado HTTP OK.
     * @throws ResourceNotFoundException si el email no existe en la ddbb.
     * @throws AccountActivationException si el usuario se encuentra inactivo.
     * @throws BusinessRuleException si las claves pasadas no coinciden.
     */
    @Validated
    @Transactional(rollbackFor={
            BusinessRuleException.class,
            AccountActivationException.class
    })
    public ResponseEntity<Response>generateANewPassword(@Valid NuevaContrasena dto) throws ResourceNotFoundException, AccountActivationException, BusinessRuleException {
        final String email= dto.email();
        final String password1=dto.contrasena();
        final String password2=dto.confirmarContrasena();
        Usuario usuario=usuarioRepository.findByEmail(email).orElseThrow(
                ()->new ResourceNotFoundException(notFoundByEmail));
        if(!isAnActiveUser(usuario))
            throw new AccountActivationException(alreadyInactivated);
        if(differentPassword(password1,password2))
            throw new BusinessRuleException(errorPassword);
        usuario.setContrasena(password1);
        usuarioRepository.saveAndFlush(usuario);
        return ResponseEntity.ok().body(responseOK);
    }
    /**
     * Desactiva una cuenta de usuario.
     *
     * @param dto objeto para desactivar la cuenta.
     * @return Respuesta de éxito con el estado HTTP ACCEPTED.
     * @throws ResourceNotFoundException si el id del usuario no existe en la ddbb.
     * @throws AccountActivationException si el usuario se encuentra inactivo.
     * @throws BusinessRuleException si la desactivación no está confirmada.
     */
    @Validated
    @Transactional(rollbackFor=BusinessRuleException.class)
    public ResponseEntity<Response>disableAccount(@Valid RemoveUsuarioDto dto) throws ResourceNotFoundException, BusinessRuleException, AccountActivationException {
        final Long id=dto.id();
        final boolean remove= dto.remove();
        Usuario usuario=usuarioRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException(notFoundByID));
        if(!remove)
            throw new BusinessRuleException(notConfirmed);
        if(!usuario.isActivo())
            throw new AccountActivationException(alreadyInactivated);
        usuarioRepository.setInactiveToUser(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseEliminated);
    }
    /**
     * Desactiva una lista de cuentas de usuario en lote.
     *
     * @param dto objeto para desactivar las cuentas.
     * @return Respuesta de éxito con el estado HTTP ACCEPTED.
     * @throws ResourceNotFoundException si algun id de usuario no existe en la ddbb.
     * @throws AccountActivationException si se encuentran usuarios inactivos.
     * @throws BusinessRuleException si la desactivación no está confirmada.
     */
    @Validated
    @Transactional(rollbackFor = {
            ResourceNotFoundException.class,
            BusinessRuleException.class,
            AccountActivationException.class
    })
    public ResponseEntity<Response>disableAccounts(@Valid RemoveListaUsuariosDto dto) throws ResourceNotFoundException, BusinessRuleException, AccountActivationException {
        final List<Long> ids=dto.ids();
        if(!existsAllUsers(ids)) {
            String errorList = Message.RESOURCES_DO_NOT_EXIST;
            throw new ResourceNotFoundException(
                    errorList +
                    printNonExistingIds(ids)
            );
        }
        if(!dto.remove())
            throw new BusinessRuleException(notConfirmed);
        if(!areAllActive(ids))
            throw new AccountActivationException(printAllInactives(ids));
        List<Usuario>usuarios=usuarioRepository.findAllById(ids);
        usuarioRepository.setInactiveToUserList(usuarios);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseEliminated);
    }
    /**
     * Reactiva una cuenta de usuario por email.
     *
     * @param email mail del usuario a reactivar.
     * @return Respuesta de éxito con el estado HTTP OK.
     * @throws ResourceNotFoundException si el email no existe en la ddbb.
     * @throws AccountActivationException si la cuenta ya está activada.
     */
    @Transactional
    public ResponseEntity<Response>reactivateAccountByEmail(String email) throws ResourceNotFoundException, AccountActivationException {
        Usuario usuario=usuarioRepository.findByEmail(email).orElseThrow(
                ()->new ResourceNotFoundException(notFoundByEmail)
        );
        if(usuario.isActivo())
            throw new AccountActivationException(alreadyActivated);
        usuario.setActivo(true);
        usuarioRepository.saveAndFlush(usuario);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responseAccepted);
    }
}