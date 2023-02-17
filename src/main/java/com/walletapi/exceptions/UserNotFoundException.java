package com.walletapi.exceptions;

import java.io.Serial;

/**
 * User not found exception.
 */
public class UserNotFoundException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  public UserNotFoundException() {
    super(ExceptionsMessages.USER_NOT_FOUND);
  }
}
