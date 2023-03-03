package com.walletapi.controller;

import com.mongodb.MongoException;
import com.walletapi.exceptions.DataError;
import com.walletapi.exceptions.ExceptionsMessages;
import com.walletapi.exceptions.ExpenseNotFoundException;
import com.walletapi.exceptions.UserNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exceptions handle.
 */
@ControllerAdvice
public class AdviceManager {

  /**
   * Username duplicity handle.
   */
  @ExceptionHandler({MongoException.class, DuplicateKeyException.class})
  public ResponseEntity<DataError> handleDuplicity(Exception e) {
    DataError errorResponse = new DataError(ExceptionsMessages.USERNAME_ALREADY_EXISTS);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<DataError> handleAuthenticationError(BadCredentialsException e) {
    DataError errorResponse = new DataError(ExceptionsMessages.INVALID_LOGIN);
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler({
      UserNotFoundException.class,
      UsernameNotFoundException.class,
      ExpenseNotFoundException.class
  })
  public ResponseEntity<DataError> handleUserNotFoundError(Exception e) {
    DataError errorResponse = new DataError(e.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<DataError> handleInvalidField(MethodArgumentNotValidException e) {
    DataError errorResponse = new DataError(e.getFieldError().getDefaultMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<DataError> handleInvalidMethodOrTag(HttpMessageNotReadableException e) {
    DataError errorResponse = new DataError(ExceptionsMessages.INVALID_TAG_METHOD);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * Internal server error handler.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<DataError> handleInternalError(Exception e) {
    DataError errorResponse = new DataError(ExceptionsMessages.INTERNAL);
    System.out.println(e.getClass());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
