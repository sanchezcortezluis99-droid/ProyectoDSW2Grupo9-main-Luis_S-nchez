package cibertec.grupo9.proyectodsw2grupo9.service.impl;

import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.ProductoDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Categoria;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Producto;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.CategoriaRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.ProductoRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AppMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDTO> listar() {
        return productoRepository.findByEstadoTrue()
                .stream()
                .map(p -> mapper.toDto(p))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO obtenerPorId(Long id) {
        return mapper.toDto(buscar(id));
    }

    @Override
    @Transactional
    public ProductoDTO registrar(ProductoDTO dto, String username) {
        if (productoRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un producto con el código " + dto.getCodigo());
        }

        Producto producto = mapper.toEntity(dto);
        producto.setIdProducto(null);
        producto.setCategoria(buscarCategoria(dto.getIdCategoria()));
        producto.setUsuario(usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username)));
        producto.setEstado(true);
        producto.setFechaRegistro(LocalDateTime.now());

        return mapper.toDto(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoDTO actualizar(Long id, ProductoDTO dto) {
        Producto producto = buscar(id);

        boolean cambioCodigo = !producto.getCodigo().equals(dto.getCodigo());
        if (cambioCodigo && productoRepository.existsByCodigo(dto.getCodigo())) {
            throw new BusinessException("Ya existe un producto con el código " + dto.getCodigo());
        }

        producto.setCodigo(dto.getCodigo());
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(buscarCategoria(dto.getIdCategoria()));
        if (dto.getEstado() != null) {
            producto.setEstado(dto.getEstado());
        }

        return mapper.toDto(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscar(id);
        producto.setEstado(false);   // borrado lógico
        productoRepository.save(producto);
    }

    private Producto buscar(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    private Categoria buscarCategoria(Long idCategoria) {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + idCategoria));
    }
}
