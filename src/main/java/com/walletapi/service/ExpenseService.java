package com.walletapi.service;

import com.walletapi.domain.ExpenseDto;
import com.walletapi.exceptions.ExpenseNotFoundException;
import com.walletapi.model.Expense;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;
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
    newExpense.setExpenseId(UUID.randomUUID());
    newExpense.setCurrency(expensePayload.getCurrency());
    newExpense.setValue(expensePayload.getValue());
    newExpense.setDescription(expensePayload.getDescription());
    newExpense.setMethod(expensePayload.getMethod());
    newExpense.setTag(expensePayload.getTag());

    user.addExpense(newExpense);
    this.userRepo.save(user);
    return newExpense;
  }

  /**
   * Expense remove method.
   */
  public void removeExpense(UUID expenseId) {
    User user = this.userService.getUserByUsername();
    Expense expenseToRemove = this.findExpenseById(user, expenseId);
    user.getExpensesList().remove(expenseToRemove);
    this.userRepo.save(user);
  }

  /**
   * Edit expense method.
   */
  public Expense editExpense(UUID expenseId, Expense payload) {
    User user = this.userService.getUserByUsername();
    Expense expenseToEdit = this.findExpenseById(user, expenseId);
    user.getExpensesList().remove(expenseToEdit);

    Optional.ofNullable(payload.getMethod()).ifPresent(expenseToEdit::setMethod);
    Optional.ofNullable(payload.getTag()).ifPresent(expenseToEdit::setTag);
    Optional.ofNullable(payload.getValue()).ifPresent(expenseToEdit::setValue);
    Optional.ofNullable(payload.getDescription()).ifPresent(expenseToEdit::setDescription);
    Optional.ofNullable(payload.getCurrency()).ifPresent(expenseToEdit::setCurrency);

    user.getExpensesList().add(expenseToEdit);
    this.userRepo.save(user);
    return expenseToEdit;
  }

  private Expense findExpenseById(User user, UUID expenseId) {
    return user
        .getExpensesList()
        .stream()
        .filter(x -> x.getExpenseId().equals(expenseId))
        .findFirst()
        .orElseThrow(ExpenseNotFoundException::new);
  }

}
