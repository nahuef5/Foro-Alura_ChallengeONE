package com.aluralatam.ForoAlura.domain.topico.model.entity;
import com.aluralatam.ForoAlura.domain.curso.models.entity.Curso;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.aluralatam.ForoAlura.domain.respuesta.model.entity.Respuesta;
import com.aluralatam.ForoAlura.domain.topico.utils.StatusTopico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of ="id")
@Entity(name="Topico")
@Table(name="topicos")
public class Topico{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String mensaje;
    private LocalDateTime fechaCreacion;
    @Enumerated(EnumType.STRING)
    private StatusTopico status = StatusTopico.NO_RESPONDIDO;

    @ManyToOne
    @JoinColumn(name="usuarioAutor_id")
    @JsonIgnore
    private Usuario autor;
    @ManyToOne
    @JoinColumn(name="curso_id")
    @JsonIgnore
    private Curso curso;
    @OneToMany(
            mappedBy ="topico",
            fetch=FetchType.EAGER,
            cascade= CascadeType.REMOVE
    )
    @JsonIgnore
    private List<Respuesta>respuestas = new ArrayList<>();
    public Topico(String titulo, String mensaje, Curso curso){
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.curso = curso;
        this.fechaCreacion= LocalDateTime.now();
    }
    public Topico(String titulo, String mensaje, Usuario usuario,Curso curso){
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.curso = curso;
        this.autor = usuario;
        this.fechaCreacion= LocalDateTime.now();
    }
    public boolean addRespuestaToList(Respuesta respuesta){
        return respuestas.add(respuesta);
    }
    public boolean removeRespuestaFromList(Respuesta respuesta){
        return respuestas.remove(respuesta);
    }
}