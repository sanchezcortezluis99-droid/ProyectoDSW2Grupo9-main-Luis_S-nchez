package cibertec.grupo9.proyectodsw2grupo9.mapper;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.*;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppMapper {

    // Rol
    RolDTO toDto(Rol rol);
    Rol toEntity(RolDTO dto);

    // Persona
    PersonaDTO toDto(Persona persona);
    Persona toEntity(PersonaDTO dto);

    // Usuario
    @Mapping(source = "rol.idRol", target = "idRol")
    @Mapping(source = "rol.nombre", target = "nombreRol")
    UsuarioDTO toDto(Usuario usuario);

    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "password", ignore = true)   // se cifra en el service
    Usuario toEntity(UsuarioDTO dto);

    // Categoria
    CategoriaDTO toDto(Categoria categoria);
    Categoria toEntity(CategoriaDTO dto);

    // Producto
    @Mapping(source = "categoria.idCategoria", target = "idCategoria")
    @Mapping(source = "categoria.nombre", target = "nombreCategoria")
    @Mapping(source = "usuario.username", target = "usernameRegistro")
    ProductoDTO toDto(Producto producto);

    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Producto toEntity(ProductoDTO dto);

    // DetalleVenta
    @Mapping(source = "producto.idProducto", target = "idProducto")
    @Mapping(source = "producto.nombre", target = "nombreProducto")
    DetalleVentaDTO toDto(DetalleVenta detalle);

    // Venta
    @Mapping(source = "usuario.username", target = "username")
    VentaDTO toDto(Venta venta);
}
