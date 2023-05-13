package com.walletapi.model;

import com.walletapi.domain.ExchangeRate;
import com.walletapi.domain.PaymentMethodEnum;
import com.walletapi.domain.TagEnum;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Expenses POJO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
  private UUID expenseId;
  private Double value;
  private String description;
  private String currency;
  private TagEnum tag;
  private PaymentMethodEnum method;
  private LocalDateTime createdAt;
  private List<ExchangeRate> exchangeRates;
}
