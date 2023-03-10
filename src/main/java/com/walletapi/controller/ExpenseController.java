package com.walletapi.controller;

import com.walletapi.domain.ExpenseDto;
import com.walletapi.model.Expense;
import com.walletapi.service.ExpenseService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expenses Controller.
 */
@CrossOrigin(maxAge = 3600, origins = "*", allowedHeaders = "*")
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

  @DeleteMapping("/{expenseId}")
  public ResponseEntity<String> removeExpense(@PathVariable UUID expenseId) {
    this.expenseService.removeExpense(expenseId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
  }

  /**
   * Edit Expense method.
   */
  @PutMapping("/{expenseId}")
  public ResponseEntity<Expense> editExpense(@PathVariable UUID expenseId,
                                             @RequestBody Expense payload) {

    Expense editedExpense = this.expenseService.editExpense(expenseId, payload);
    return ResponseEntity.ok(editedExpense);
  }

}
