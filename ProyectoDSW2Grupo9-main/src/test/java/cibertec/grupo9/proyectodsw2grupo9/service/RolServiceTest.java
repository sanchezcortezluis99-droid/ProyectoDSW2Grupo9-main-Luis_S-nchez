package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.RolDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Rol;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.RolRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.impl.RolServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AppMapper mapper;

    @InjectMocks
    private RolServiceImpl rolService;

    private Rol rolAdmin;
    private Rol rolSupervisor;
    private RolDTO rolAdminDTO;
    private RolDTO rolSupervisorDTO;

    @BeforeEach
    void setUp() {
        rolAdmin = new Rol(1L, "ADMIN");
        rolSupervisor = new Rol(3L, "SUPERVISOR");

        rolAdminDTO = new RolDTO(1L, "ADMIN");
        rolSupervisorDTO = new RolDTO(3L, "SUPERVISOR");
    }

    @Test
    @DisplayName("Listar roles retorna lista mapeada")
    void testListar() {
        when(rolRepository.findAll()).thenReturn(List.of(rolAdmin, rolSupervisor));
        when(mapper.toDto(rolAdmin)).thenReturn(rolAdminDTO);
        when(mapper.toDto(rolSupervisor)).thenReturn(rolSupervisorDTO);

        List<RolDTO> resultado = rolService.listar();

        assertEquals(2, resultado.size());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener por ID existente retorna DTO")
    void testObtenerPorIdExito() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));
        when(mapper.toDto(rolAdmin)).thenReturn(rolAdminDTO);

        RolDTO resultado = rolService.obtenerPorId(1L);

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getNombre());
    }

    @Test
    @DisplayName("Obtener por ID inexistente lanza ResourceNotFoundException")
    void testObtenerPorIdNoExiste() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> rolService.obtenerPorId(99L));
    }

    @Test
    @DisplayName("Registrar nuevo rol exitosamente")
    void testRegistrarExito() {
        RolDTO nuevoDTO = new RolDTO(null, "cajero");
        Rol rolEntity = new Rol(null, "CAJERO");
        Rol rolGuardado = new Rol(4L, "CAJERO");
        RolDTO rolGuardadoDTO = new RolDTO(4L, "CAJERO");

        when(rolRepository.existsByNombreIgnoreCase("CAJERO")).thenReturn(false);
        when(mapper.toEntity(nuevoDTO)).thenReturn(rolEntity);
        when(rolRepository.save(any(Rol.class))).thenReturn(rolGuardado);
        when(mapper.toDto(rolGuardado)).thenReturn(rolGuardadoDTO);

        RolDTO resultado = rolService.registrar(nuevoDTO);

        assertNotNull(resultado);
        assertEquals(4L, resultado.getIdRol());
        assertEquals("CAJERO", resultado.getNombre());
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("Registrar rol con nombre duplicado lanza BusinessException")
    void testRegistrarDuplicado() {
        RolDTO dto = new RolDTO(null, "ADMIN");
        when(rolRepository.existsByNombreIgnoreCase("ADMIN")).thenReturn(true);

        assertThrows(BusinessException.class, () -> rolService.registrar(dto));
        verify(rolRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar rol exitosamente")
    void testActualizarExito() {
        RolDTO updateDTO = new RolDTO(3L, "JEFE_ALMACEN");
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolSupervisor));
        when(rolRepository.findByNombreIgnoreCase("JEFE_ALMACEN")).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(rolSupervisor);
        when(mapper.toDto(rolSupervisor)).thenReturn(new RolDTO(3L, "JEFE_ALMACEN"));

        RolDTO resultado = rolService.actualizar(3L, updateDTO);

        assertNotNull(resultado);
        assertEquals("JEFE_ALMACEN", resultado.getNombre());
    }

    @Test
    @DisplayName("Actualizar nombre de rol de sistema ADMIN lanza BusinessException")
    void testActualizarRolSistema() {
        RolDTO updateDTO = new RolDTO(1L, "SUPER_ADMIN");
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        assertThrows(BusinessException.class, () -> rolService.actualizar(1L, updateDTO));
    }

    @Test
    @DisplayName("Eliminar rol asignado a usuarios lanza BusinessException")
    void testEliminarRolAsignado() {
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolSupervisor));
        when(usuarioRepository.existsByRolIdRol(3L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> rolService.eliminar(3L));
        verify(rolRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Eliminar rol de sistema lanza BusinessException")
    void testEliminarRolSistema() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolAdmin));

        assertThrows(BusinessException.class, () -> rolService.eliminar(1L));
        verify(rolRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Eliminar rol no asignado tiene éxito")
    void testEliminarRolExito() {
        when(rolRepository.findById(3L)).thenReturn(Optional.of(rolSupervisor));
        when(usuarioRepository.existsByRolIdRol(3L)).thenReturn(false);

        assertDoesNotThrow(() -> rolService.eliminar(3L));
        verify(rolRepository, times(1)).delete(rolSupervisor);
    }
}
