package com.aluralatam.ForoAlura.domain.usuario.model.entity;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateUsuarioDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import jakarta.persistence.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Usuario")
@Table(name="usuarios")
public class Usuario{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private DatoPersonal dato;
    private String email;
    private String contrasena;
    private boolean activo;
    public Usuario(CreateUsuarioDTO dto){
        this.dato=new DatoPersonal(dto.datosPersonalesDto());
        this.email=dto.email();
        this.contrasena= dto.contrasena();
        activo=true;
    }
    public Usuario(DatoPersonal dato,String email,String contrasena){
        this.dato=dato;
        this.email=email;
        this.contrasena=contrasena;
        activo=true;
    }
}