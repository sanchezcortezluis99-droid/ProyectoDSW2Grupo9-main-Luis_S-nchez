package cibertec.grupo9.proyectodsw2grupo9.security;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/v1/api/auth/**", "/error").permitAll()

                        // Categorías: todos leen, solo ADMIN modifica
                        .requestMatchers(HttpMethod.GET, "/api/categorias/**", "/v1/api/categorias/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers("/api/categorias/**", "/v1/api/categorias/**").hasRole("ADMIN")

                        // Productos: solo ADMIN elimina
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**", "/v1/api/productos/**").hasRole("ADMIN")
                        .requestMatchers("/api/productos/**", "/v1/api/productos/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Ventas: ambos roles
                        .requestMatchers("/api/ventas/**", "/v1/api/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")

                        // Usuarios y roles: solo ADMIN
                        .requestMatchers("/api/usuarios/**", "/v1/api/usuarios/**", "/api/roles/**", "/v1/api/roles/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
