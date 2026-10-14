package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.VentaDTO;

import java.util.List;

public interface VentaService {
    VentaDTO registrar(VentaDTO dto, String username);
    List<VentaDTO> listar(String username, boolean isAdmin);
    List<VentaDTO> listarMisVentas(String username);
    VentaDTO obtenerPorId(Long id, String username, boolean isAdmin);
    VentaDTO anular(Long id, String username, boolean isAdmin);
    void eliminar(Long id);
}
