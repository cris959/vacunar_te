package com.cris959.Vacunar_te.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 1. Recursos estaticos y Login siempre libres
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        .requestMatchers("/login").permitAll()

                        // 2. Reglas por Rol (De lo mas especifico a lo mas general)
                        .requestMatchers("/laboratorios/**").hasRole("ADMIN")
                        .requestMatchers("/vacunas/**").hasAnyRole("ADMIN", "USER")

                        // 3. Lo demas requiere estar logueado
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/ciudadanos/listado", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // La ruta que dispara el cierre
                        .logoutSuccessUrl("/login?logout") // A donde va despues
                        .invalidateHttpSession(true) // Borra la sesion de la memoria
                        .clearAuthentication(true) // Borra los datos del usuario actual
                        .deleteCookies("JSESSIONID") // Borra la cookie del navegador
                        .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptacion segura para las claves en la DB
    }
}
