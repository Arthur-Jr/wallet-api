package com.walletapi.unit.service;

import com.walletapi.domain.ExpenseDto;
import com.walletapi.exceptions.ExpenseNotFoundException;
import com.walletapi.exceptions.UserNotFoundException;
import com.walletapi.model.Expense;
import com.walletapi.model.User;
import com.walletapi.repositories.UserRepository;
import com.walletapi.service.ExpenseService;
import com.walletapi.service.UserService;
import com.walletapi.util.ExpenseDataExample;
import com.walletapi.util.UserDataExample;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@DisplayName("Expense service tests")
public class ExpenseServiceTests {
  @InjectMocks
  private ExpenseService expenseService;

  @Mock
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  private final ExpenseDto expenseDto = new ExpenseDto();
  private final Expense expense = new Expense();
  private final User user = new User();

  @BeforeEach
  void initEach() {
    this.expenseDto.setValue(ExpenseDataExample.value);
    this.expenseDto.setTag(ExpenseDataExample.tag);
    this.expenseDto.setMethod(ExpenseDataExample.method);
    this.expenseDto.setDescription(ExpenseDataExample.description);
    this.expenseDto.setCurrency(ExpenseDataExample.currency);

    this.user.setUsername(UserDataExample.USERNAME);
    this.user.setPassword(UserDataExample.ENCODED_PASSWORD);

    this.expense.setValue(ExpenseDataExample.value);
    this.expense.setTag(ExpenseDataExample.tag);
    this.expense.setMethod(ExpenseDataExample.method);
    this.expense.setDescription(ExpenseDataExample.description);
    this.expense.setCurrency(ExpenseDataExample.currency);
    this.expense.setExpenseId(UUID.randomUUID());

    this.user.addExpense(this.expense);
  }

  @Test
  @DisplayName("Add expense tests: should add new expense and return it")
  void should_add_new_expense_and_return() {
    when(this.userRepository.save(any(User.class))).thenReturn(this.user);
    when(this.userService.getUserByUsername()).thenReturn(this.user);
    Expense newExpense = this.expenseService.addExpense(this.expenseDto);

    assertNotNull(newExpense);
    assertNotNull(newExpense.getExpenseId());
    assertNotNull(newExpense.getCreatedAt());
    assertEquals(newExpense.getValue(), this.expenseDto.getValue());
    assertEquals(newExpense.getDescription(), this.expenseDto.getDescription());
    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  @DisplayName("Add expense tests: should throw if user not found")
  void should_throw_if_user_not_found() {
    when(this.userService.getUserByUsername()).thenThrow(UserNotFoundException.class);

    Throwable exception = assertThrows(
        UserNotFoundException.class, () -> this.expenseService.addExpense(this.expenseDto)
    );
    assertNotNull(exception);
    verify(this.userRepository, times(0)).save(any(User.class));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  @DisplayName("Remove expense tests: Should remove expense by expense id")
  void should_remove_one_expense() {
    when(this.userService.getUserByUsername()).thenReturn(this.user);
    when(this.userRepository.save(any(User.class))).thenReturn(this.user);

    this.expenseService.removeExpense(this.expense.getExpenseId());

    verify(this.userRepository, times(1)).save(any(User.class));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  @DisplayName("Remove expense tests: Should throw if expense not found")
  void should_throw_if_expense_not_found() {
    when(this.userService.getUserByUsername()).thenReturn(this.user);

    Throwable exception = assertThrows(
        ExpenseNotFoundException.class, () -> this.expenseService.removeExpense(UUID.randomUUID())
    );
    assertNotNull(exception);
    verify(this.userRepository, times(0)).save(any(User.class));
    verify(this.userService, times(1)).getUserByUsername();
  }

  @Test
  void test() {
    Expense expensePayload = new Expense();
    expensePayload.setValue(50.00);
    expensePayload.setDescription("Edited expense");
    when(this.userService.getUserByUsername()).thenReturn(this.user);
    when(this.userRepository.save(any(User.class))).thenReturn(this.user);

    Expense editedEx = this.expenseService.editExpense(this.expense.getExpenseId(), expensePayload);

    assertNotNull(editedEx);
    assertEquals(this.expense.getExpenseId(), editedEx.getExpenseId());
    assertEquals(expensePayload.getValue(), editedEx.getValue());
    assertEquals(expensePayload.getDescription(), editedEx.getDescription());
    assertEquals(this.expense.getCurrency(), editedEx.getCurrency());
    assertEquals(this.expense.getMethod(), editedEx.getMethod());
    assertEquals(this.expense.getTag(), editedEx.getTag());
    assertEquals(this.expense.getCreatedAt(), editedEx.getCreatedAt());
    verify(this.userService, times(1)).getUserByUsername();
    verify(this.userRepository, times(1)).save(any(User.class));
  }
}
