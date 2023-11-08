package com.aluralatam.ForoAlura.domain.respuesta.model.entity;
import com.aluralatam.ForoAlura.domain.topico.model.entity.Topico;
import com.aluralatam.ForoAlura.domain.usuario.model.entity.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name="Respuesta")
@Table(name="respuestas")
public class Respuesta{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String mensaje;

    @ManyToOne
    @JoinColumn(name="usuarioAutor_id")
    @JsonIgnore
    private Usuario autor;
    @ManyToOne
    @JoinColumn(name="topico_id")
    @JsonIgnore
    private Topico topico;

    private LocalDateTime fechaCreacion;
    private Boolean solucion = false;
    public Respuesta(String mensaje,Usuario usuario,Topico topico){
        this.mensaje = mensaje;
        this.autor=usuario;
        this.topico=topico;
        this.fechaCreacion = LocalDateTime.now();
    }
}