package com.helptask.service;

import com.helptask.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UsuarioService {
    Usuario findByEmail(String email);

    Usuario createOrUpdate(Usuario usuario);

    Optional<Usuario> findById(String id);

    void delete(String id);

    Page<Usuario> findAll(int page, int count);
}
