package cibertec.grupo9.proyectodsw2grupo9.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaDTO {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idDetalle;

    @NotNull
    private Long idProducto;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreProducto;

    @NotNull
    @Positive
    private Integer cantidad;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal precioUnitario;   // lo pone el backend desde Producto

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal subtotal;
}
