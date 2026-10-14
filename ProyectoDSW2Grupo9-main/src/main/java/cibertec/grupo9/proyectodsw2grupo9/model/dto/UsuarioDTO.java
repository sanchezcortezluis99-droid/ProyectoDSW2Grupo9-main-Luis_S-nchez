package cibertec.grupo9.proyectodsw2grupo9.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;

    @NotBlank
    @Size(max = 50)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, max = 50)
    private String password;

    private Boolean estado;

    @NotNull
    @Valid
    private PersonaDTO persona;      // se registra junto con el usuario

    @NotNull
    private Long idRol;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nombreRol;

}
