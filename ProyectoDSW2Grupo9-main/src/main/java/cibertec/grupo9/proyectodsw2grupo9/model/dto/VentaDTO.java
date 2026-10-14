package cibertec.grupo9.proyectodsw2grupo9.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idVenta;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fecha;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String username;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String estado;

    @NotEmpty(message = "El carrito está vacío")
    @Valid
    private List<DetalleVentaDTO> detalles;
}
