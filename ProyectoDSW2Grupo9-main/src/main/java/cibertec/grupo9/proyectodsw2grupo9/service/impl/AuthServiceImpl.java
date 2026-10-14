package cibertec.grupo9.proyectodsw2grupo9.service.impl;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.AuthDTO;
import cibertec.grupo9.proyectodsw2grupo9.model.entity.Usuario;
import cibertec.grupo9.proyectodsw2grupo9.reposiroty.UsuarioRepository;
import cibertec.grupo9.proyectodsw2grupo9.security.JwtService;
import cibertec.grupo9.proyectodsw2grupo9.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public AuthDTO login(AuthDTO dto) {
        // compara la contraseña con el hash BCrypt; si no coincide lanza BadCredentialsException
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername()).orElseThrow();
        String rol = usuario.getRol().getNombre();
        String token = jwtService.generarToken(usuario.getUsername(), rol);

        return new AuthDTO(usuario.getUsername(), null, token, rol);
    }
}
