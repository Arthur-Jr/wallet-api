package com.walletapi.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT Authentication filter.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserDetailsService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    final String headerFieldName = "Authorization";
    final String tokenPrefix = "Bearer ";

    String authHeader = request.getHeader(headerFieldName);

    if (authHeader == null || !authHeader.startsWith(tokenPrefix)) {
      filterChain.doFilter(request, response);
      return;
    }

    String jwtToken = authHeader.substring(7); /* Prefix remove */
    String username = this.jwtService.extractUsername(jwtToken);

    // Check if user is not already authenticated.
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      this.setTokenOnSecurityContext(jwtToken, username, request);
    }

    filterChain.doFilter(request, response);
  }

  private void setTokenOnSecurityContext(String jwtToken, String username, HttpServletRequest req) {
    UserDetails user = this.userService.loadUserByUsername(username);

    if (this.jwtService.isTokenValid(jwtToken, user)) {
      UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
          user.getUsername(),
          null,
          new ArrayList<>()
      );

      token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
      SecurityContextHolder.getContext().setAuthentication(token);
    }
  }

}
