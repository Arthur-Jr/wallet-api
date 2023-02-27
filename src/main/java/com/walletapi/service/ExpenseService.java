package com.walletapi.service;

import com.walletapi.domain.ExpenseDto;
import com.walletapi.model.Expense;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Expenses service.
 */
@Service
public class ExpenseService {
  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepo;

  /**
   * Add expense method.
   */
  public Expense addExpense(ExpenseDto expensePayload) {
    User user = this.userService.getUserByUsername();
    Expense newExpense = new Expense();
    newExpense.setExpenseId(user.getExpensesList().size());
    newExpense.setCurrency(expensePayload.getCurrency());
    newExpense.setValue(expensePayload.getValue());
    newExpense.setDescription(expensePayload.getDescription());
    newExpense.setMethod(expensePayload.getMethod());
    newExpense.setTag(expensePayload.getTag());

    user.addExpense(newExpense);
    this.userRepo.save(user);
    return newExpense;
  }
}
