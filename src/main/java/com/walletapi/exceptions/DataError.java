package com.walletapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Default error response.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataError {
  private String message;
}
