package com.helptask.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Qualifier("jwtUserDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
      String authToken = httpServletRequest.getHeader("Authorization");
      String username = jwtTokenUtil.getUserNameFromToken(authToken);
      if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
          UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
          if(jwtTokenUtil.validateToken(authToken,userDetails)){
              UsernamePasswordAuthenticationToken autentication = new UsernamePasswordAuthenticationToken(
                      userDetails, null, userDetails.getAuthorities()
              );
              autentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
              logger.info("authenticated user "+username + ", setting security context");
              SecurityContextHolder.getContext().setAuthentication(autentication);
          }
      }
      filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
