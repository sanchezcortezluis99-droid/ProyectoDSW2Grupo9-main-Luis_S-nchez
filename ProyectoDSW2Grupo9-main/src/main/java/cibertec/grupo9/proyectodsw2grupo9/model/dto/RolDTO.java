package cibertec.grupo9.proyectodsw2grupo9.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Long idRol;

    @NotBlank
    @Size(max = 30)
    private String nombre;
}
