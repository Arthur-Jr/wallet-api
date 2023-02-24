package com.walletapi.service;

import com.walletapi.exceptions.ExceptionsMessages;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * User details service.
 */
@Service
public class UserDetailsServiceImp implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = this.userRepository.findByUsername(username);

    if (user == null) {
      throw new UsernameNotFoundException(ExceptionsMessages.USER_NOT_FOUND);
    }

    return user;
  }
}
