package com.walletapi.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.walletapi.configure.ApplicationConfig;
import com.walletapi.configure.WebSecurityConfig;
import com.walletapi.controller.UserController;
import com.walletapi.domain.TokenResponse;
import com.walletapi.domain.UserDto;
import com.walletapi.exceptions.ExceptionsMessages;
import com.walletapi.exceptions.UserNotFoundException;
import com.walletapi.jwt.JwtService;
import com.walletapi.model.User;
import com.walletapi.service.UserDetailsServiceImp;
import com.walletapi.service.UserService;
import com.walletapi.util.UserDataExample;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({WebSecurityConfig.class, ApplicationConfig.class, JwtService.class})
@DisplayName("User controller tests:")
public class UserControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserDetailsServiceImp userDetailsService;

  private final JwtService jwtService = new JwtService(
      "25432A462D4A614E645267556B58703273357638782F413F4428472B4B625065"
  );

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
  @DisplayName("Register user: Should have status code 201 and return jwt token")
  void register_user_success_case() throws Exception {
    when(userService.registerUser(this.userDto)).thenReturn(new TokenResponse(this.JWT_TOKEN_EX));
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").exists());
    verify(this.userService, times(1)).registerUser(this.userDto);
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(any(String.class));
  }

  @Test
  @DisplayName("Register user: Should have status code 409 and return duplicity error message")
  void register_user_duplicity_error_case() throws Exception {
    when(userService.registerUser(this.userDto)).thenThrow(MongoException.class);
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.USERNAME_ALREADY_EXISTS));
    verify(this.userService, times(1)).registerUser(this.userDto);
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(any(String.class));
  }

  @Test
  @DisplayName("Get user by username tests: should have status 200 and return user data")
  void get_user_by_username_success_case() throws Exception {
    when(this.userService.getUserByUsername()).thenReturn(this.user);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    String token = this.jwtService.generateToken(this.user);
    ResultActions response = this.getUserByUsername(token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(this.user.getUsername()));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  @DisplayName("Get user by username tests: should have status 404 and return user not found message")
  void get_user_by_username_not_found_error_case() throws Exception {
    when(this.userService.getUserByUsername()).thenThrow(new UserNotFoundException());
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    String token = this.jwtService.generateToken(this.user);
    ResultActions response = this.getUserByUsername(token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.USER_NOT_FOUND));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  @DisplayName("Get user by username tests: should have status 403 and if theres no token")
  void get_user_by_username_forbidden_error_case() throws Exception {
    when(this.userService.getUserByUsername()).thenThrow(new UserNotFoundException());
    ResultActions response = this.getUserByUsername(null);

    response.andExpect(status().isForbidden());
    verify(this.userService, times(0)).getUserByUsername();
  }

  @Test
  @DisplayName("login tests: should have status 200 and return a JWT token")
  void login_authentication_success_case() throws Exception {
    when(this.userService.login(this.userDto)).thenReturn(new TokenResponse(this.JWT_TOKEN_EX));
    ResultActions response = this.login(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
    verify(this.userService, times(1)).login(this.userDto);
  }

  @Test
  @DisplayName("login tests: should have status 403 and return invalid login message")
  void login_authentication_error_case() throws Exception {
    when(this.userService.login(this.userDto)).thenThrow(BadCredentialsException.class);
    ResultActions response = this.login(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.INVALID_LOGIN));
    verify(this.userService, times(1)).login(this.userDto);
  }

  @Test
  @DisplayName("Username validations tests: Invalid format email test")
  void invalid_email_test() throws Exception {
    this.userDto.setUsername("test");
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.INVALID_EMAIL));
  }

  @Test
  @DisplayName("Username validations tests: empty email test")
  void empty_email_test() throws Exception {
    this.userDto.setUsername(null);
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_USERNAME));
  }

  @Test
  @DisplayName("Password validations tests: invalid password test")
  void invalid_password_test() throws Exception {
    this.userDto.setPassword("test");
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.PASSWORD_SIZE));
  }

  @Test
  @DisplayName("Password validations tests: empty password test")
  void empty_password_test() throws Exception {
    this.userDto.setPassword(null);
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_PASSWORD));
  }


  private ResultActions login(UserDto payload) throws Exception {
    return this.mockMvc.perform(post("/user/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload)
        ));
  }

  private ResultActions getUserByUsername(String token) throws Exception {
    if (token != null) {
      return this.mockMvc.perform(get("/user")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON));
    }

    return this.mockMvc.perform(get("/user").contentType(MediaType.APPLICATION_JSON));
  }

  private ResultActions registerUser(UserDto payload) throws Exception {
    return this.mockMvc.perform(post("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload))
    );
  }
}
