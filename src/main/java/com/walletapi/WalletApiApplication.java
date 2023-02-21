package com.walletapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * main class.
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class WalletApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(WalletApiApplication.class, args);
  }

  @Bean
  public PasswordEncoder getPassword() {
    return new BCryptPasswordEncoder();
  }
}
