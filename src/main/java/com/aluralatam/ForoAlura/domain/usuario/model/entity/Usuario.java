package com.aluralatam.ForoAlura.domain.usuario.model.entity;
import com.aluralatam.ForoAlura.domain.respuesta.model.entity.Respuesta;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateUsuarioDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.utils.DatoPersonal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
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
    @ManyToMany(mappedBy = "usuarios")
    private List<Curso> cursos = new ArrayList<>();
    @OneToMany(
            mappedBy="autor",
            fetch=FetchType.LAZY,
            cascade= CascadeType.REMOVE
    )
    @JsonIgnore
    List<Topico> topicos=new ArrayList<>();
    @OneToMany(
            mappedBy ="autor",
            fetch=FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    private List<Respuesta> respuestas=new ArrayList<>();
    public Usuario(CreateUsuarioDTO dto){
        this.dato=new DatoPersonal(dto.datosPersonales());
        this.email=dto.email();
        this.contrasena= dto.contrasena();
        activo=true;
    }
    public Usuario(DatoPersonal dato,String email,boolean activo){
        this.dato=dato;
        this.email=email;
        this.activo=activo;
    }
    public Usuario(DatoPersonal dato,String email,String contrasena){
        this.dato=dato;
        this.email=email;
        this.contrasena=contrasena;
        activo=true;
    }
    public boolean addCursoToList(Curso curso){
        return cursos.add(curso);
    }
    public boolean removeCursoFromList(Curso curso){
        return cursos.remove(curso);
    }
    public boolean addTopicoToList(Topico topico){
        return topicos.add(topico);
    }
    public boolean addRespuestaToList(Respuesta respuesta){
        return respuestas.add(respuesta);
    }
}