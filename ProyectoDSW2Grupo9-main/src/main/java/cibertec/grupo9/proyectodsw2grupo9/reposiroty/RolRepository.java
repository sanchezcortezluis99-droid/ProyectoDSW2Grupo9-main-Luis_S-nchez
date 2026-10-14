package cibertec.grupo9.proyectodsw2grupo9.reposiroty;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
    Optional<Rol> findByNombreIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
