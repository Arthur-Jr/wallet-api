package com.walletapi.model;

import com.walletapi.domain.PaymentMethodEnum;
import com.walletapi.domain.TagEnum;
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

}
