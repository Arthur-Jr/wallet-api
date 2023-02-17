package com.walletapi.domain;

import com.walletapi.exceptions.ExceptionsMessages;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * user DTO.
 */
public class UserDto {
  @NotEmpty(message = ExceptionsMessages.EMPTY_USERNAME)
  @Size(min = 3, message = ExceptionsMessages.USERNAME_SIZE)
  private String username;
  
  @NotEmpty(message = ExceptionsMessages.EMPTY_PASSWORD)
  private String password;

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}
