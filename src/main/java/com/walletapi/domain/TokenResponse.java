package com.walletapi.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token Response model class.
 */
@Data
@NoArgsConstructor
public class TokenResponse {
  private String token;

  public TokenResponse(String token) {
    this.token = token;
  }
}
