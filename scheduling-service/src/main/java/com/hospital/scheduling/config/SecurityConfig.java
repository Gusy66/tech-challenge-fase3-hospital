package com.hospital.scheduling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration                   // Indica que esta classe contém configurações do Spring
@EnableWebSecurity               // Habilita a configuração personalizada do Spring Security
@EnableMethodSecurity            // Permite usar @PreAuthorize nos métodos dos controllers
public class SecurityConfig {

    @Bean // Define o filtro de segurança HTTP — controla quem acessa o quê
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())              // Desabilita CSRF (API stateless não precisa)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Sem sessão no servidor
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()     // Endpoints de login/registro são públicos
                .requestMatchers("/graphql").authenticated() // GraphQL requer autenticação
                .anyRequest().authenticated()                // Todo o resto requer autenticação
            )
            .httpBasic(basic -> {});  // Habilita autenticação básica (HTTP Basic Auth)

        return http.build();
    }

    @Bean // Codificador de senhas — usa BCrypt para hash seguro
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
