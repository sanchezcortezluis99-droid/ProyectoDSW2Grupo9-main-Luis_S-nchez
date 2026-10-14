package cibertec.grupo9.proyectodsw2grupo9.controller;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.RolDTO;
import cibertec.grupo9.proyectodsw2grupo9.service.RolService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolControllerTest {

    @Mock
    private RolService rolService;

    @InjectMocks
    private RolController rolController;

    @Test
    @DisplayName("GET /api/roles retorna 200 y lista")
    void testListar() {
        when(rolService.listar()).thenReturn(List.of(
                new RolDTO(1L, "ADMIN"),
                new RolDTO(2L, "VENDEDOR")
        ));

        ResponseEntity<List<RolDTO>> response = rolController.listar();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        verify(rolService, times(1)).listar();
    }

    @Test
    @DisplayName("GET /api/roles/{id} retorna 200 y rol")
    void testObtener() {
        when(rolService.obtenerPorId(1L)).thenReturn(new RolDTO(1L, "ADMIN"));

        ResponseEntity<RolDTO> response = rolController.obtener(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ADMIN", response.getBody().getNombre());
    }

    @Test
    @DisplayName("POST /api/roles retorna 201 Created")
    void testRegistrar() {
        RolDTO request = new RolDTO(null, "SUPERVISOR");
        RolDTO created = new RolDTO(3L, "SUPERVISOR");
        when(rolService.registrar(request)).thenReturn(created);

        ResponseEntity<RolDTO> response = rolController.registrar(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getIdRol());
        assertEquals("SUPERVISOR", response.getBody().getNombre());
    }

    @Test
    @DisplayName("PUT /api/roles/{id} retorna 200 OK")
    void testActualizar() {
        RolDTO request = new RolDTO(3L, "JEFE_ALMACEN");
        when(rolService.actualizar(3L, request)).thenReturn(request);

        ResponseEntity<RolDTO> response = rolController.actualizar(3L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("JEFE_ALMACEN", response.getBody().getNombre());
    }

    @Test
    @DisplayName("DELETE /api/roles/{id} retorna 204 No Content")
    void testEliminar() {
        doNothing().when(rolService).eliminar(3L);

        ResponseEntity<Void> response = rolController.eliminar(3L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rolService, times(1)).eliminar(3L);
    }
}
