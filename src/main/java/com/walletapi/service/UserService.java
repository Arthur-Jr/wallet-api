package com.walletapi.service;

import com.walletapi.domain.UserDto;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User service.
 */
@Service
public class UserService {
  @Autowired
  private UserRepository userRepo;

  /**
   * Register new user method.
   */
  public User registerUser(UserDto userPayload) {
    User user = new User();
    user.setUsername(userPayload.getUsername());
    user.setPassword(userPayload.getPassword());

    return this.userRepo.save(user);
  }
}