package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.RolDTO;

import java.util.List;

public interface RolService {
    List<RolDTO> listar();
    RolDTO obtenerPorId(Long id);
    RolDTO registrar(RolDTO dto);
    RolDTO actualizar(Long id, RolDTO dto);
    void eliminar(Long id);
}
