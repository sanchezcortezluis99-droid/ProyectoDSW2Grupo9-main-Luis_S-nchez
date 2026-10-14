package cibertec.grupo9.proyectodsw2grupo9.model.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long idCategoria;

    @NotBlank
    @Size(max = 60)
    private String nombre;

    @Size(max = 200)
    private String descripcion;

    private Boolean estado;

}
