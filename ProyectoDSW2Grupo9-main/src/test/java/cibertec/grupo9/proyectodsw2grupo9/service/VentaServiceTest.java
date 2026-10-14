package cibertec.grupo9.proyectodsw2grupo9.service;

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
import cibertec.grupo9.proyectodsw2grupo9.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AppMapper mapper;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Usuario usuario;
    private Producto productoMouse;
    private Producto productoTeclado;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .idUsuario(1L)
                .username("vendedor")
                .estado(true)
                .build();

        productoMouse = Producto.builder()
                .idProducto(10L)
                .codigo("P001")
                .nombre("Mouse Óptico")
                .precio(new BigDecimal("25.00"))
                .stock(20)
                .estado(true)
                .build();

        productoTeclado = Producto.builder()
                .idProducto(20L)
                .codigo("P002")
                .nombre("Teclado Mecánico")
                .precio(new BigDecimal("100.00"))
                .stock(5)
                .estado(true)
                .build();
    }

    @Test
    @DisplayName("Registrar venta descuenta stock y calcula precios en el backend")
    void testRegistrarVentaExito() {
        VentaDTO dto = new VentaDTO();
        DetalleVentaDTO d1 = new DetalleVentaDTO(null, 10L, null, 2, null, null);
        DetalleVentaDTO d2 = new DetalleVentaDTO(null, 20L, null, 1, null, null);
        dto.setDetalles(List.of(d1, d2));

        when(usuarioRepository.findByUsername("vendedor")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMouse));
        when(productoRepository.findById(20L)).thenReturn(Optional.of(productoTeclado));

        ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
        when(ventaRepository.save(ventaCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        VentaDTO responseDto = new VentaDTO();
        responseDto.setIdVenta(1L);
        responseDto.setTotal(new BigDecimal("150.00"));
        responseDto.setEstado("CONFIRMADA");
        when(mapper.toDto(any(Venta.class))).thenReturn(responseDto);

        VentaDTO resultado = ventaService.registrar(dto, "vendedor");

        assertNotNull(resultado);
        Venta ventaGuardada = ventaCaptor.getValue();
        // Mouse: 2 * 25.00 = 50.00
        // Teclado: 1 * 100.00 = 100.00
        // Total esperado: 150.00
        assertEquals(new BigDecimal("150.00"), ventaGuardada.getTotal());
        assertEquals("CONFIRMADA", ventaGuardada.getEstado());
        assertEquals(usuario, ventaGuardada.getUsuario());

        // Stock descontado:
        assertEquals(18, productoMouse.getStock());
        assertEquals(4, productoTeclado.getStock());

        verify(productoRepository, times(1)).save(productoMouse);
        verify(productoRepository, times(1)).save(productoTeclado);
    }

    @Test
    @DisplayName("Registrar venta con stock insuficiente lanza BusinessException")
    void testRegistrarStockInsuficiente() {
        VentaDTO dto = new VentaDTO();
        DetalleVentaDTO d = new DetalleVentaDTO(null, 20L, null, 10, null, null); // pide 10, solo hay 5
        dto.setDetalles(List.of(d));

        when(usuarioRepository.findByUsername("vendedor")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(20L)).thenReturn(Optional.of(productoTeclado));

        assertThrows(BusinessException.class, () -> ventaService.registrar(dto, "vendedor"));

        // Aseguramos que el stock no fue alterado
        assertEquals(5, productoTeclado.getStock());
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar venta con producto inactivo lanza BusinessException")
    void testRegistrarProductoInactivo() {
        productoMouse.setEstado(false);
        VentaDTO dto = new VentaDTO();
        DetalleVentaDTO d = new DetalleVentaDTO(null, 10L, null, 1, null, null);
        dto.setDetalles(List.of(d));

        when(usuarioRepository.findByUsername("vendedor")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMouse));

        assertThrows(BusinessException.class, () -> ventaService.registrar(dto, "vendedor"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registrar venta agrupa productos duplicados en el carrito")
    void testRegistrarConsolidaDuplicados() {
        VentaDTO dto = new VentaDTO();
        DetalleVentaDTO d1 = new DetalleVentaDTO(null, 10L, null, 2, null, null);
        DetalleVentaDTO d2 = new DetalleVentaDTO(null, 10L, null, 3, null, null); // total 5 de mouse
        dto.setDetalles(List.of(d1, d2));

        when(usuarioRepository.findByUsername("vendedor")).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(10L)).thenReturn(Optional.of(productoMouse));

        ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
        when(ventaRepository.save(ventaCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(Venta.class))).thenReturn(new VentaDTO());

        ventaService.registrar(dto, "vendedor");

        Venta ventaGuardada = ventaCaptor.getValue();
        assertEquals(1, ventaGuardada.getDetalles().size());
        assertEquals(5, ventaGuardada.getDetalles().get(0).getCantidad());
        assertEquals(15, productoMouse.getStock()); // 20 - 5 = 15
    }

    @Test
    @DisplayName("Anular venta restablece inventario y actualiza estado a ANULADA")
    void testAnularVentaExito() {
        Venta venta = new Venta();
        venta.setIdVenta(50L);
        venta.setEstado("CONFIRMADA");
        venta.setUsuario(usuario);

        DetalleVenta detalle = DetalleVenta.builder()
                .venta(venta)
                .producto(productoMouse)
                .cantidad(4)
                .precioUnitario(new BigDecimal("25.00"))
                .subtotal(new BigDecimal("100.00"))
                .build();

        List<DetalleVenta> detalles = new ArrayList<>();
        detalles.add(detalle);
        venta.setDetalles(detalles);

        when(ventaRepository.findById(50L)).thenReturn(Optional.of(venta));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(mapper.toDto(venta)).thenReturn(new VentaDTO());

        // Stock actual de mouse es 20
        ventaService.anular(50L, "vendedor", false);

        assertEquals("ANULADA", venta.getEstado());
        assertEquals(24, productoMouse.getStock()); // 20 + 4
        verify(productoRepository, times(1)).save(productoMouse);
        verify(ventaRepository, times(1)).save(venta);
    }

    @Test
    @DisplayName("Anular venta ya anulada lanza BusinessException")
    void testAnularVentaYaAnulada() {
        Venta venta = new Venta();
        venta.setIdVenta(50L);
        venta.setEstado("ANULADA");
        venta.setUsuario(usuario);

        when(ventaRepository.findById(50L)).thenReturn(Optional.of(venta));

        assertThrows(BusinessException.class, () -> ventaService.anular(50L, "vendedor", false));
    }

    @Test
    @DisplayName("Eliminar venta físicamente lanza BusinessException")
    void testEliminarVentaProhibida() {
        assertThrows(BusinessException.class, () -> ventaService.eliminar(1L));
    }
}
