package com.walletapi.unit.service;

import com.mongodb.MongoException;
import com.walletapi.domain.TokenResponse;
import com.walletapi.domain.UserDto;
import com.walletapi.exceptions.UserNotFoundException;
import com.walletapi.jwt.JwtService;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import com.walletapi.service.UserService;
import com.walletapi.util.UserDataExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service tests:")
public class UserServiceTests {
  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder encoder;

  private final UserDto userDto = new UserDto();
  private final User user = new User();
  private final String JWT_TOKEN_EX = "token";

  @BeforeEach
  public void initEach() {
    this.userDto.setUsername(UserDataExample.USERNAME);
    this.userDto.setPassword(UserDataExample.PASSWORD);

    this.user.setUsername(UserDataExample.USERNAME);
    this.user.setPassword(UserDataExample.ENCODED_PASSWORD);
  }

  @Test
  @DisplayName("Register user test: get userDto and return a Jwt token")
  void should_register_new_user() {
    when(this.encoder.encode(any(String.class))).thenReturn(UserDataExample.ENCODED_PASSWORD);
    when(this.userRepository.save(any(User.class))).thenReturn(this.user);
    when(this.jwtService.generateToken(this.user)).thenReturn(this.JWT_TOKEN_EX);
    TokenResponse token = this.userService.registerUser(this.userDto);

    assertNotNull(token);
    assertEquals(token.getClass(), TokenResponse.class);
    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.jwtService, times(1)).generateToken(this.user);
    verify(this.encoder, times(1)).encode(this.userDto.getPassword());
  }

  @Test
  @DisplayName("Register user test: Throw a duplicity error if email already registered")
  void should_throw_duplicity_error() {
    when(this.encoder.encode(any(String.class))).thenReturn(UserDataExample.ENCODED_PASSWORD);
    when(this.userRepository.save(any(User.class))).thenThrow(MongoException.class);

    Throwable exception = assertThrows(
        MongoException.class, () -> this.userService.registerUser(this.userDto)
    );
    assertNotNull(exception);
    assertEquals(exception.getClass(), MongoException.class);
    verify(this.encoder, times(1)).encode(this.userDto.getPassword());
    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.jwtService, times(0)).generateToken(this.user);
  }

  @Test
  @DisplayName("Find user by username test: should find user by username on jwt")
  void should_find_user_by_username() {
    this.mockSecurityContextHolder();
    when(this.userRepository.findByUsername(UserDataExample.USERNAME)).thenReturn(this.user);

    User user = this.userService.getUserByUsername();
    assertEquals(user.getUsername(), this.user.getUsername());
    verify(this.userRepository, times(1))
        .findByUsername(UserDataExample.USERNAME);
    verify(SecurityContextHolder.getContext().getAuthentication(), times(1))
        .getPrincipal();
  }

  @Test
  @DisplayName("Find user by username test: should throw if user not found")
  void should_throw_if_user_not_found() {
    this.mockSecurityContextHolder();
    when(this.userRepository.findByUsername(UserDataExample.USERNAME))
        .thenThrow(UserNotFoundException.class);

    Throwable exception = assertThrows(
        UserNotFoundException.class, () -> this.userService.getUserByUsername()
    );

    assertNotNull(exception);
    verify(SecurityContextHolder.getContext().getAuthentication(), times(1))
        .getPrincipal();
    verify(this.userRepository, times(1))
        .findByUsername(UserDataExample.USERNAME);
  }

  private void mockSecurityContextHolder() {
    Authentication auth = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(auth);
    SecurityContextHolder.setContext(securityContext);
    when(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
        .thenReturn(UserDataExample.USERNAME);
  }
}