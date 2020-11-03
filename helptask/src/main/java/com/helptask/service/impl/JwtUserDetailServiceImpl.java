package com.helptask.service.impl;

import com.helptask.entity.Usuario;
import com.helptask.security.jwt.JwtUserFactory;
import com.helptask.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailServiceImpl implements UserDetailsService {
   @Autowired
   private UsuarioService usuarioService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioService.findByEmail(email);
        if(usuario == null){
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.",email));
        }else{
            return JwtUserFactory.create(usuario);
        }
    }
}
