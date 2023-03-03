package com.walletapi.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.walletapi.configure.ApplicationConfig;
import com.walletapi.configure.WebSecurityConfig;
import com.walletapi.controller.UserController;
import com.walletapi.domain.TokenResponse;
import com.walletapi.domain.UserDto;
import com.walletapi.exceptions.ExceptionsMessages;
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
  @DisplayName("Register user: Should have status code 400 and return an error message")
  void register_user_error_case() throws Exception {
    when(userService.registerUser(this.userDto)).thenThrow(MongoException.class);
    ResultActions response = this.registerUser(this.userDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.USERNAME_ALREADY_EXISTS));
    verify(this.userService, times(1)).registerUser(this.userDto);
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(any(String.class));
  }

  private ResultActions registerUser(UserDto payload) throws Exception {
    return this.mockMvc.perform(post("/user")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload))
    );
  }
}
