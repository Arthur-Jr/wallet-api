package com.walletapi.domain;

import com.walletapi.exceptions.ExceptionsMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
  @NotNull(message = ExceptionsMessages.EMPTY_VALUE)
  private Double value;
  @NotEmpty(message = ExceptionsMessages.EMPTY_DESCRIPTION)
  private String description;
  @NotEmpty(message = ExceptionsMessages.EMPTY_CURRENCY)
  private String currency;
  @NotNull(message = ExceptionsMessages.EMPTY_TAG)
  private TagEnum tag;
  @NotNull(message = ExceptionsMessages.EMPTY_METHOD)
  private PaymentMethodEnum method;
}
