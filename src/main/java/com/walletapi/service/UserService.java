package com.walletapi.service;

import com.walletapi.domain.TokenResponse;
import com.walletapi.domain.UserDto;
import com.walletapi.exceptions.UserNotFoundException;
import com.walletapi.jwt.JwtService;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {
  @Autowired
  private UserRepository userRepo;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  /**
   * Register new user method.
   */
  public TokenResponse registerUser(UserDto userPayload) {
    User user = new User();
    user.setUsername(userPayload.getUsername());
    user.setPassword(this.encoder.encode(userPayload.getPassword()));
    User userDetail = this.userRepo.save(user);

    String jwtToken = this.jwtService.generateToken(userDetail);
    return new TokenResponse(jwtToken);
  }

  /**
   * Get a user by username method.
   */
  public User getUserByUsername() {
    String username = (String) SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();

    User user = this.userRepo.findByUsername(username);
    if (user == null) {
      throw new UserNotFoundException();
    }

    return user;
  }

  /**
   * User login method.
   */
  public TokenResponse login(UserDto payload) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(payload.getUsername(), payload.getPassword())
    );

    User user = this.userRepo.findByUsername(payload.getUsername());
    return new TokenResponse(this.jwtService.generateToken(user));
  }

}
