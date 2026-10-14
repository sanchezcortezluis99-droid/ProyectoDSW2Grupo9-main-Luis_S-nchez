package cibertec.grupo9.proyectodsw2grupo9.controller;


import cibertec.grupo9.proyectodsw2grupo9.model.dto.AuthDTO;
import cibertec.grupo9.proyectodsw2grupo9.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDTO> login(@Valid @RequestBody AuthDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
