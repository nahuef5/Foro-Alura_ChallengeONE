package com.aluralatam.ForoAlura.domain.usuario.model.utils;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.CreateDatoPersonalDTO;
import com.aluralatam.ForoAlura.domain.usuario.model.dto.UpdateDatoPersonalDTO;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatoPersonal{
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String pais;
    private String provincia;
    private String localidad;
    public DatoPersonal(CreateDatoPersonalDTO dto){
        this.nombre=dto.nombre();
        this.apellido=dto.apellido();
        this.fechaNacimiento=dto.fechaNacimiento();
        this.pais=dto.pais();
        this.provincia=dto.provincia();
        this.localidad= dto.localidad();
    }
}