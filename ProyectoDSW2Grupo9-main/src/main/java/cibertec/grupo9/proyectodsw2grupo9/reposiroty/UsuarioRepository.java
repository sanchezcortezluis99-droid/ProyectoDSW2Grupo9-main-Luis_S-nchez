package cibertec.grupo9.proyectodsw2grupo9.reposiroty;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);   // para el login
    boolean existsByUsername(String username);
    boolean existsByRolIdRol(Long idRol);
}
