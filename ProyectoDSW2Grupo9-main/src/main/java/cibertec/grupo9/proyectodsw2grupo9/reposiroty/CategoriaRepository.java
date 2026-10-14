package cibertec.grupo9.proyectodsw2grupo9.reposiroty;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByEstadoTrue();
}
