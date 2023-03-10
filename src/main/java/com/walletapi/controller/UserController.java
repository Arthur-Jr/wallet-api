package com.walletapi.controller;

import com.walletapi.domain.TokenResponse;
import com.walletapi.domain.UserDto;
import com.walletapi.model.User;
import com.walletapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * User controller.
 */
@CrossOrigin(maxAge = 3600, origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserService service;

  @PostMapping
  public ResponseEntity<TokenResponse> registerUser(@Valid @RequestBody UserDto userPayload) {
    TokenResponse token = this.service.registerUser(userPayload);
    return ResponseEntity.status(HttpStatus.CREATED).body(token);
  }

  @GetMapping
  public User getUserByUsername() {
    return this.service.getUserByUsername();
  }

  @PostMapping("/login")
  public TokenResponse login(@Valid @RequestBody UserDto userPayload) {
    return this.service.login(userPayload);
  }

}
