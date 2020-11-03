package com.helptask.service.impl;

import com.helptask.entity.Usuario;
import com.helptask.repository.UsuarioRepository;
import com.helptask.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServiceImp implements UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Override
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override
    public Usuario createOrUpdate(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(String id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public void delete(String id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Page<Usuario> findAll(int page, int count) {
        Pageable pageable = PageRequest.of(page, count);
        return usuarioRepository.findAll(pageable);
    }
}
