package cibertec.grupo9.proyectodsw2grupo9.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {

    private Long idProducto;

    @NotBlank
    @Size(max = 30)
    private String codigo;

    @NotBlank
    @Size(max = 100)
    private String nombre;

    @Size(max = 250)
    private String descripcion;

    @NotNull
    @Positive
    private BigDecimal precio;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    private Boolean estado;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaRegistro;

    @NotNull
    private Long idCategoria;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreCategoria;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String usernameRegistro;   // se toma del token, no del front
}
