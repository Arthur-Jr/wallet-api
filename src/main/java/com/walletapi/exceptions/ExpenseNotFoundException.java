package com.walletapi.exceptions;

import java.io.Serial;

/**
 * Expense not found exception.
 */
public class ExpenseNotFoundException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1L;

  public ExpenseNotFoundException() {
    super(ExceptionsMessages.EXPENSE_NOT_FOUND);
  }
}
