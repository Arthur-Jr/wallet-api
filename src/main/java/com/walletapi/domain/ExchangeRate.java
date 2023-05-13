package com.walletapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Exchange rate body.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
  private String code;
  private String codein;
  private String name;
  private String bid;
  private String ask;
}
