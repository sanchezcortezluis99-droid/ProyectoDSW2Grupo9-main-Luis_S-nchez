package cibertec.grupo9.proyectodsw2grupo9.reposiroty;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findAllByOrderByFechaDesc();
    List<Venta> findByUsuarioUsernameOrderByFechaDesc(String username);
}
