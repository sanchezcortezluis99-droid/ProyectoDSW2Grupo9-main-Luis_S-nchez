package cibertec.grupo9.proyectodsw2grupo9.service.impl;

import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.DetalleVentaDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.VentaDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.DetalleVenta;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Producto;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Usuario;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Venta;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.ProductoRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.VentaRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AppMapper mapper;

    @Override
    @Transactional
    public VentaDTO registrar(VentaDTO dto, String username) {
        if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
            throw new BusinessException("El carrito no contiene productos");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con username: " + username));

        // Consolidar cantidades por producto para evitar duplicados en la misma orden
        Map<Long, Integer> cantidadesPorProducto = new LinkedHashMap<>();
        for (DetalleVentaDTO item : dto.getDetalles()) {
            if (item.getIdProducto() == null) {
                throw new BusinessException("El identificador del producto es obligatorio");
            }
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new BusinessException("La cantidad debe ser mayor a cero");
            }
            cantidadesPorProducto.merge(item.getIdProducto(), item.getCantidad(), Integer::sum);
        }

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());
        venta.setEstado("CONFIRMADA");

        BigDecimal total = BigDecimal.ZERO;
        List<DetalleVenta> detallesEntidad = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cantidadesPorProducto.entrySet()) {
            Long idProducto = entry.getKey();
            Integer cantidad = entry.getValue();

            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + idProducto));

            if (Boolean.FALSE.equals(producto.getEstado())) {
                throw new BusinessException("El producto '" + producto.getNombre() + "' está inactivo y no puede venderse");
            }

            if (producto.getStock() < cantidad) {
                throw new BusinessException("Stock insuficiente para el producto '" + producto.getNombre()
                        + "'. Solicitado: " + cantidad + ", Disponible: " + producto.getStock());
            }

            // El precio unitario SIEMPRE se toma del backend
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            total = total.add(subtotal);

            // Descontar inventario
            producto.setStock(producto.getStock() - cantidad);
            productoRepository.save(producto);

            DetalleVenta detalle = DetalleVenta.builder()
                    .venta(venta)
                    .producto(producto)
                    .cantidad(cantidad)
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build();

            detallesEntidad.add(detalle);
        }

        venta.setTotal(total);
        venta.setDetalles(detallesEntidad);

        Venta guardada = ventaRepository.save(venta);
        return mapper.toDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> listar(String username, boolean isAdmin) {
        if (isAdmin) {
            return ventaRepository.findAllByOrderByFechaDesc()
                    .stream()
                    .map(mapper::toDto)
                    .toList();
        }
        return listarMisVentas(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDTO> listarMisVentas(String username) {
        return ventaRepository.findByUsuarioUsernameOrderByFechaDesc(username)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorId(Long id, String username, boolean isAdmin) {
        Venta venta = buscar(id);
        if (!isAdmin && !venta.getUsuario().getUsername().equals(username)) {
            throw new BusinessException("No tiene permisos para consultar esta venta");
        }
        return mapper.toDto(venta);
    }

    @Override
    @Transactional
    public VentaDTO anular(Long id, String username, boolean isAdmin) {
        Venta venta = buscar(id);

        if (!isAdmin && !venta.getUsuario().getUsername().equals(username)) {
            throw new BusinessException("No tiene permisos para anular esta venta");
        }

        if ("ANULADA".equalsIgnoreCase(venta.getEstado())) {
            throw new BusinessException("La venta #" + id + " ya se encuentra anulada");
        }

        // Restablecer el inventario de los productos de cada detalle
        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = detalle.getProducto();
                producto.setStock(producto.getStock() + detalle.getCantidad());
                productoRepository.save(producto);
            }
        }

        venta.setEstado("ANULADA");
        return mapper.toDto(ventaRepository.save(venta));
    }

    @Override
    public void eliminar(Long id) {
        throw new BusinessException("No se permite eliminar físicamente ventas confirmadas para preservar la auditoría y la consistencia del inventario. Utilice la opción de anulación.");
    }

    private Venta buscar(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
    }
}
