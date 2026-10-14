package cibertec.grupo9.proyectodsw2grupo9.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(int status,
                            String mensaje,
                            Map<String, String> errores,
                            LocalDateTime fecha) {
}
