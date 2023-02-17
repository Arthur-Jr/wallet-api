package com.walletapi.controller;

import com.walletapi.domain.UserDto;
import com.walletapi.model.User;
import com.walletapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User controller.
 */
@RestController
@RequestMapping("/user")
public class UserController {
  @Autowired
  private UserService service;

  @PostMapping
  public ResponseEntity<User> registerUser(@Valid @RequestBody UserDto userPayload) {
    User newUser = this.service.registerUser(userPayload);
    return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
  }
}
