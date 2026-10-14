package cibertec.grupo9.proyectodsw2grupo9.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonaDTO {

    private Long idPersona;

    @NotBlank
    @Size(max = 80)
    private String nombres;

    @NotBlank
    @Size(max = 80)
    private String apellidos;

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Email
    @Size(max = 100)
    private String correo;

    @Size(max = 15)
    private String telefono;
}
