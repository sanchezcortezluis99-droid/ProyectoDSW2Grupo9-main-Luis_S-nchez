package cibertec.grupo9.proyectodsw2grupo9.service.impl;

import cibertec.grupo9.proyectodsw2grupo9.exception.BusinessException;
import cibertec.grupo9.proyectodsw2grupo9.exception.ResourceNotFoundException;
import cibertec.grupo9.proyectodsw2grupo9.mapper.AppMapper;
import cibertec.grupo9.proyectodsw2grupo9.model.dto.RolDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Rol;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.RolRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final AppMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> listar() {
        return rolRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO obtenerPorId(Long id) {
        return mapper.toDto(buscar(id));
    }

    @Override
    @Transactional
    public RolDTO registrar(RolDTO dto) {
        String nombreNormalizado = normalizarNombre(dto.getNombre());

        if (rolRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new BusinessException("Ya existe un rol con el nombre " + nombreNormalizado);
        }

        Rol rol = mapper.toEntity(dto);
        rol.setIdRol(null);
        rol.setNombre(nombreNormalizado);

        return mapper.toDto(rolRepository.save(rol));
    }

    @Override
    @Transactional
    public RolDTO actualizar(Long id, RolDTO dto) {
        Rol rol = buscar(id);
        String nombreNormalizado = normalizarNombre(dto.getNombre());

        if (esRolDelSistema(rol.getNombre()) && !rol.getNombre().equalsIgnoreCase(nombreNormalizado)) {
            throw new BusinessException("No se puede modificar el nombre de los roles predeterminados del sistema (ADMIN, VENDEDOR)");
        }

        rolRepository.findByNombreIgnoreCase(nombreNormalizado)
                .filter(r -> !r.getIdRol().equals(id))
                .ifPresent(r -> {
                    throw new BusinessException("Ya existe otro rol con el nombre " + nombreNormalizado);
                });

        rol.setNombre(nombreNormalizado);
        return mapper.toDto(rolRepository.save(rol));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscar(id);

        if (esRolDelSistema(rol.getNombre())) {
            throw new BusinessException("No se pueden eliminar los roles predeterminados del sistema (ADMIN, VENDEDOR)");
        }

        if (usuarioRepository.existsByRolIdRol(id)) {
            throw new BusinessException("No se puede eliminar el rol porque está asignado a uno o más usuarios.");
        }

        rolRepository.delete(rol);
    }

    private Rol buscar(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con id " + id));
    }

    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new BusinessException("El nombre del rol es obligatorio");
        }
        return nombre.trim().toUpperCase();
    }

    private boolean esRolDelSistema(String nombre) {
        return "ADMIN".equalsIgnoreCase(nombre) || "VENDEDOR".equalsIgnoreCase(nombre);
    }
}
