package com.walletapi.domain;

import com.walletapi.exceptions.ExceptionsMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * user DTO.
 */
@Data
public class UserDto {
  @NotEmpty(message = ExceptionsMessages.EMPTY_USERNAME)
  @Email(message = ExceptionsMessages.INVALID_EMAIL,
      regexp = "(^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$)")
  private String username;

  @NotEmpty(message = ExceptionsMessages.EMPTY_PASSWORD)
  @Size(min = 6, message = ExceptionsMessages.PASSWORD_SIZE)
  private String password;
}
