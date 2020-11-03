package com.helptask.security.controller;

import com.helptask.entity.Usuario;
import com.helptask.security.jwt.JwtAuthenticationRequest;
import com.helptask.security.jwt.JwtTokenUtil;
import com.helptask.security.model.CurrentUser;
import com.helptask.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "*")
public class AuthenticationRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Qualifier("jwtUserDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping(value = "/api/auth")
    @Operation(summary = "endpoint que gera o token", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<?> createAutenticationToken(@RequestBody JwtAuthenticationRequest jwtAuthenticationRequest) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        jwtAuthenticationRequest.getEmail(),
                        jwtAuthenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtAuthenticationRequest.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails);
        Usuario usuario = usuarioService.findByEmail(jwtAuthenticationRequest.getEmail());
        usuario.setPassword(null);
        return ResponseEntity.ok(new CurrentUser(token, usuario));
    }

    @PostMapping(value = "/api/refresh")
    @Operation(summary = "endpoint que atualiza o token", security = @SecurityRequirement(name = "Authorization"))
    public ResponseEntity<?> refreshAutenticationToken(HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        String username = jwtTokenUtil.getUserNameFromToken(token);
        final Usuario usuario = usuarioService.findByEmail(username);
        if (jwtTokenUtil.canTokenBeRefreshed(token)) {
            String refreshToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new CurrentUser(refreshToken, usuario));
        }
        return ResponseEntity.badRequest().body(null);
    }


}
