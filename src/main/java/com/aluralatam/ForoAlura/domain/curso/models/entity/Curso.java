package com.aluralatam.ForoAlura.domain.curso.models.entity;
import com.aluralatam.ForoAlura.domain.curso.models.dtos.CUCursoDto;
import jakarta.persistence.*;
import lombok.*;
@Data
@Builder
@NoArgsConstructor
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

    public Curso(CUCursoDto dto) {
        this.nombre = dto.nombre();
        this.categoria = dto.categoria();
        this.inactivo=false;
    }
    public Curso(String nombre, String categoria){
        this.nombre=nombre;
        this.categoria=categoria;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Curso other = (Curso) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}