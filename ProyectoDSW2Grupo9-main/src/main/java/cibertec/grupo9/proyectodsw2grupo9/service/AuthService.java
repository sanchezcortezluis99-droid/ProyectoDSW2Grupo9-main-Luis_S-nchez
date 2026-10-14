package cibertec.grupo9.proyectodsw2grupo9.service;

import cibertec.grupo9.proyectodsw2grupo9.model.dto.AuthDTO;

public interface AuthService {
    AuthDTO login(AuthDTO dto);
}
