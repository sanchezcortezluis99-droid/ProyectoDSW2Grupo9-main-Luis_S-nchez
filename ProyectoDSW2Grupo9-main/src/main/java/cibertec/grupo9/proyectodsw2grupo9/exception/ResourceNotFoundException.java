package cibertec.grupo9.proyectodsw2grupo9.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }
}
