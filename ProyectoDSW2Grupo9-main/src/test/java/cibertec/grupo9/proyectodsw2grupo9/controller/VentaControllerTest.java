package cibertec.grupo9.proyectodsw2grupo9.controller;

import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.DetalleVentaDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.VentaDTO;
import cibertec.grupo9.proyectodsw2grupo9.service.VentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    @Mock
    private VentaService ventaService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VentaController ventaController;

    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {
        DetalleVentaDTO detalle = new DetalleVentaDTO(1L, 10L, "Mouse", 2, new BigDecimal("25.00"), new BigDecimal("50.00"));
        ventaDTO = new VentaDTO(100L, LocalDateTime.now(), new BigDecimal("50.00"), "vendedor", "CONFIRMADA", List.of(detalle));
    }

    @Test
    @DisplayName("POST /api/ventas registra y retorna 201 Created")
    void testRegistrarVenta() {
        when(authentication.getName()).thenReturn("vendedor");
        when(ventaService.registrar(any(VentaDTO.class), eq("vendedor"))).thenReturn(ventaDTO);

        ResponseEntity<VentaDTO> response = ventaController.registrar(new VentaDTO(), authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(100L, response.getBody().getIdVenta());
        assertEquals(new BigDecimal("50.00"), response.getBody().getTotal());
    }

    @Test
    @DisplayName("GET /api/ventas como ADMIN lista todas las ventas")
    void testListarComoAdmin() {
        when(authentication.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
        when(ventaService.listar("admin", true)).thenReturn(List.of(ventaDTO));

        ResponseEntity<List<VentaDTO>> response = ventaController.listar(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(ventaService).listar("admin", true);
    }

    @Test
    @DisplayName("GET /api/ventas/mis-ventas retorna ventas del usuario autenticado")
    void testListarMisVentas() {
        when(authentication.getName()).thenReturn("vendedor");
        when(ventaService.listarMisVentas("vendedor")).thenReturn(List.of(ventaDTO));

        ResponseEntity<List<VentaDTO>> response = ventaController.listarMisVentas(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(ventaService).listarMisVentas("vendedor");
    }

    @Test
    @DisplayName("GET /api/ventas/{id} retorna venta")
    void testObtenerPorId() {
        when(authentication.getName()).thenReturn("vendedor");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_VENDEDOR")))
                .when(authentication).getAuthorities();
        when(ventaService.obtenerPorId(100L, "vendedor", false)).thenReturn(ventaDTO);

        ResponseEntity<VentaDTO> response = ventaController.obtener(100L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100L, response.getBody().getIdVenta());
    }

    @Test
    @DisplayName("PATCH /api/ventas/{id}/anular retorna venta anulada")
    void testAnularVentaPatch() {
        VentaDTO anulada = new VentaDTO(100L, LocalDateTime.now(), new BigDecimal("50.00"), "vendedor", "ANULADA", List.of());
        when(authentication.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
        when(ventaService.anular(100L, "admin", true)).thenReturn(anulada);

        ResponseEntity<VentaDTO> response = ventaController.anularPatch(100L, authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ANULADA", response.getBody().getEstado());
    }

    @Test
    @DisplayName("DELETE /api/ventas/{id} invoca servicio y arroja BusinessException")
    void testEliminarVentaLanzaExcepcion() {
        doThrow(new BusinessException("No se permite eliminar físicamente ventas confirmadas"))
                .when(ventaService).eliminar(100L);

        assertThrows(BusinessException.class, () -> ventaController.eliminar(100L));
    }
}
