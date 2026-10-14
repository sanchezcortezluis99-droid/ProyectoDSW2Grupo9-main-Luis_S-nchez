package cibertec.grupo9.proyectodsw2grupo9.config;

import cibertec.grupo9.proyectodsw2grupo9.model.entity.Categoria;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Persona;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Rol;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Usuario;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.CategoriaRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.RolRepository;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder  passwordEncoder;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(String... args) {
        Rol admin = crearRolSiNoExiste("ADMIN");
        Rol vendedor = crearRolSiNoExiste("VENDEDOR");

        crearUsuarioSiNoExiste("admin", "Administrador", "del Sistema", "00000000", admin);
        crearUsuarioSiNoExiste("vendedor", "Vendedor", "de Prueba", "11111111", vendedor);

        if (categoriaRepository.count() == 0) {
            categoriaRepository.save(new Categoria(null, "Periféricos", "Teclados, mouse y similares", true));
            categoriaRepository.save(new Categoria(null, "Almacenamiento", "USB, discos y memorias", true));
        }
    }

    private void crearUsuarioSiNoExiste(String username, String nombres, String apellidos, String dni, Rol rol) {
        Optional<Usuario> existente = usuarioRepository.findByUsername(username);

        if (existente.isPresent()) {
            Usuario usuario = existente.get();
            usuario.setPassword(passwordEncoder.encode(adminPassword));   // solo para desarrollo
            usuario.setEstado(true);
            usuarioRepository.save(usuario);
            return;
        }

        Persona persona = new Persona();
        persona.setNombres(nombres);
        persona.setApellidos(apellidos);
        persona.setDni(dni);

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(adminPassword));
        usuario.setEstado(true);
        usuario.setPersona(persona);
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> rolRepository.save(new Rol(null, nombre)));
    }
}
