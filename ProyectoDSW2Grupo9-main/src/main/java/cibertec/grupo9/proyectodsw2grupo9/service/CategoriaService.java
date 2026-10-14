package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.CategoriaDTO;

import java.util.List;

public interface CategoriaService {

    List<CategoriaDTO> listar();
    CategoriaDTO obtenerPorId(Long id);
    CategoriaDTO registrar(CategoriaDTO dto);
    CategoriaDTO actualizar(Long id, CategoriaDTO dto);
    void eliminar(Long id);
}
