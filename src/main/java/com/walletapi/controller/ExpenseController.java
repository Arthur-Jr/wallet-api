package com.walletapi.controller;

import com.walletapi.domain.ExpenseDto;
import com.walletapi.model.Expense;
import com.walletapi.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expenses Controller.
 */
@RestController
@RequestMapping("/expense")
public class ExpenseController {
  @Autowired
  private ExpenseService expenseService;

  /**
   * Add Expense method.
   */
  @PostMapping
  public ResponseEntity<Expense> addExpense(@Valid @RequestBody ExpenseDto expensePayload) {
    return ResponseEntity.status(HttpStatus.CREATED).body(
        this.expenseService.addExpense(expensePayload)
    );
  }
}
