package com.walletapi.controller;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.walletapi.exceptions.DataError;
import com.walletapi.exceptions.ExceptionsMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  @ExceptionHandler(MongoException.class)
  public ResponseEntity<DataError> handleDuplicity(MongoWriteException e) {
    DataError errorResponse = new DataError(ExceptionsMessages.USERNAME_ALREADY_EXISTS);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
