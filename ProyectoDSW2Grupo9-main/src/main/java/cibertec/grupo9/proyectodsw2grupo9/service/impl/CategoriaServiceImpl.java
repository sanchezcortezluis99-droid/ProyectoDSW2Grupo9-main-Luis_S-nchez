package cibertec.grupo9.proyectodsw2grupo9.service.impl;

import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.CategoriaDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Categoria;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.CategoriaRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {


    private final CategoriaRepository categoriaRepository;
    private final AppMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDTO> listar() {
        return categoriaRepository.findByEstadoTrue()
                .stream()
                .map(c -> mapper.toDto(c))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDTO obtenerPorId(Long id) {
        return mapper.toDto(buscar(id));
    }

    @Override
    @Transactional
    public CategoriaDTO registrar(CategoriaDTO dto) {
        Categoria categoria = mapper.toEntity(dto);
        categoria.setIdCategoria(null);   // evita que el front sobrescriba un registro existente
        categoria.setEstado(true);
        return mapper.toDto(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public CategoriaDTO actualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = buscar(id);
        categoria.setNombre(dto.getNombre());
        categoria.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }
        return mapper.toDto(categoriaRepository.save(categoria));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscar(id);
        categoria.setEstado(false);   // borrado lógico
        categoriaRepository.save(categoria);
    }

    private Categoria buscar(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + id));
    }


}
