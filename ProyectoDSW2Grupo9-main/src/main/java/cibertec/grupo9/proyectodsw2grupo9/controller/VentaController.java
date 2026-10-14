package cibertec.grupo9.proyectodsw2grupo9.controller;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.VentaDTO;
import cibertec.grupo9.proyectodsw2grupo9.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/ventas", "/v1/api/ventas"})
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaDTO> registrar(@Valid @RequestBody VentaDTO dto, Authentication authentication) {
        VentaDTO registrada = ventaService.registrar(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(registrada);
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listar(Authentication authentication) {
        boolean esAdmin = esUsuarioAdmin(authentication);
        return ResponseEntity.ok(ventaService.listar(authentication.getName(), esAdmin));
    }

    @GetMapping("/mis-ventas")
    public ResponseEntity<List<VentaDTO>> listarMisVentas(Authentication authentication) {
        return ResponseEntity.ok(ventaService.listarMisVentas(authentication.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtener(@PathVariable Long id, Authentication authentication) {
        boolean esAdmin = esUsuarioAdmin(authentication);
        return ResponseEntity.ok(ventaService.obtenerPorId(id, authentication.getName(), esAdmin));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<VentaDTO> anularPatch(@PathVariable Long id, Authentication authentication) {
        boolean esAdmin = esUsuarioAdmin(authentication);
        return ResponseEntity.ok(ventaService.anular(id, authentication.getName(), esAdmin));
    }

    @PutMapping("/{id}/anular")
    public ResponseEntity<VentaDTO> anularPut(@PathVariable Long id, Authentication authentication) {
        boolean esAdmin = esUsuarioAdmin(authentication);
        return ResponseEntity.ok(ventaService.anular(id, authentication.getName(), esAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private boolean esUsuarioAdmin(Authentication authentication) {
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }
}
