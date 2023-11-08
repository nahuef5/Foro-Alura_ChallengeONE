package com.aluralatam.ForoAlura.domain.curso.models.entity;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@Entity(name="Curso")
@Table(name="cursos")
public class Curso{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String categoria;
    private boolean inactivo;
    @ManyToMany
    @JoinTable(
            name= "usuario_curso",
            joinColumns =@JoinColumn(name="curso_id"),
            inverseJoinColumns =@JoinColumn(name="usuario_id")
    )
    @JsonIgnore
    private List<Usuario> usuarios = new ArrayList<>();
    @OneToMany(
            mappedBy ="curso",
            fetch=FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    private List<Topico> topicos=new ArrayList<>();
    public Curso(CUCursoDto dto) {
        this.nombre = dto.nombre();
        this.categoria = dto.categoria();
        this.inactivo=false;
    }
    public Curso(String nombre, String categoria){
        this.nombre=nombre;
        this.categoria=categoria;
    }
    public boolean addUsuarioToList(Usuario usuario){
        return usuarios.add(usuario);
    }
    public boolean addTopicoToList(Topico topico){
        return topicos.add(topico);
    }
    public boolean removeUsuarioFromList(Usuario usuario){
        return usuarios.remove(usuario);
    }
    public boolean removeTopicoFromList(Topico topico){
        return usuarios.remove(topico);
    }
}