package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Este procedimiento es CRUCIAL: Spring Security lo busca para la autenticacion
    UserDetails findByLogin(String login);
}
