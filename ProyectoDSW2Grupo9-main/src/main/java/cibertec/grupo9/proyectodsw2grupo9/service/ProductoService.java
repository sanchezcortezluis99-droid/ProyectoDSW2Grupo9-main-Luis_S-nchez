package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.ProductoDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoDTO> listar();
    ProductoDTO obtenerPorId(Long id);
    ProductoDTO registrar(ProductoDTO dto, String username);
    ProductoDTO actualizar(Long id, ProductoDTO dto);
    void eliminar(Long id);
}
