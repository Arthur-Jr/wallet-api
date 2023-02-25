package com.walletapi.configure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Application configs.
 */
@Configuration
public class ApplicationConfig {

  @Autowired
  private UserDetailsService userDetailsServiceImp;

  /**
   * Password Encoder.
   */
  @Bean
  public PasswordEncoder getPassword() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Create an Authentication provider.
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(this.userDetailsServiceImp);
    authProvider.setPasswordEncoder(this.getPassword());
    return authProvider;
  }

  /**
   * Get Authentication manager.
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config.getAuthenticationManager();
  }

}
