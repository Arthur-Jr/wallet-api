package com.walletapi.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletapi.configure.ApplicationConfig;
import com.walletapi.configure.WebSecurityConfig;
import com.walletapi.controller.ExpenseController;
import com.walletapi.domain.ExpenseDto;
import com.walletapi.exceptions.ExceptionsMessages;
import com.walletapi.exceptions.ExpenseNotFoundException;
import com.walletapi.exceptions.UserNotFoundException;
import com.walletapi.jwt.JwtService;
import com.walletapi.model.Expense;
import com.walletapi.model.User;
import com.walletapi.service.ExpenseService;
import com.walletapi.service.UserDetailsServiceImp;
import com.walletapi.util.ExpenseDataExample;
import com.walletapi.util.UserDataExample;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ExpenseController.class)
@Import({WebSecurityConfig.class, ApplicationConfig.class, JwtService.class})
@DisplayName("Expenses controller tests:")
public class ExpenseControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ExpenseService expenseService;

  @MockBean
  private UserDetailsServiceImp userDetailsService;

  private final JwtService jwtService = new JwtService(
      "25432A462D4A614E645267556B58703273357638782F413F4428472B4B625065"
  );

  private String token;

  private final ExpenseDto expenseDto = new ExpenseDto();
  private final Expense expense = new Expense();
  private final User user = new User();

  @BeforeEach
  public void initEach() {
    this.expenseDto.setValue(ExpenseDataExample.value);
    this.expenseDto.setTag(ExpenseDataExample.tag);
    this.expenseDto.setMethod(ExpenseDataExample.method);
    this.expenseDto.setDescription(ExpenseDataExample.description);
    this.expenseDto.setCurrency(ExpenseDataExample.currency);

    this.expense.setValue(ExpenseDataExample.value);
    this.expense.setTag(ExpenseDataExample.tag);
    this.expense.setMethod(ExpenseDataExample.method);
    this.expense.setDescription(ExpenseDataExample.description);
    this.expense.setCurrency(ExpenseDataExample.currency);
    this.expense.setExpenseId(UUID.randomUUID());

    this.user.setUsername(UserDataExample.USERNAME);
    this.user.setPassword(UserDataExample.ENCODED_PASSWORD);
    this.user.addExpense(this.expense);

    token = this.jwtService.generateToken(this.user);
  }

  @Test
  @DisplayName("Add new expense tests: should have status 201 and return a new expense")
  void add_new_expense_success_case() throws Exception {
    when(this.expenseService.addExpense(this.expenseDto)).thenReturn(this.expense);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.expenseId").exists());
    verify(this.expenseService, times(1)).addExpense(this.expenseDto);
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Add new expense tests: should have status 403 if there is no token")
  void add_new_expense_forbidden_error_case() throws Exception {
    when(this.expenseService.addExpense(this.expenseDto)).thenReturn(this.expense);
    ResultActions response = this.addNewExpense(this.expenseDto, null);

    response.andExpect(status().isForbidden());
    verify(this.expenseService, times(0)).addExpense(this.expenseDto);
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Remove expense tests: should have status 204 and return nothing")
  void remove_expense_success_case() throws Exception {
    doNothing().when(this.expenseService).removeExpense(this.expense.getExpenseId());
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.removeExpense(this.expense.getExpenseId(), this.token);

    response.andExpect(status().isNoContent());
    verify(this.expenseService, times(1))
        .removeExpense(this.expense.getExpenseId());
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Remove expense tests: should have status 404 if user not found")
  void remove_expense_not_found_user_error_case() throws Exception {
    doThrow(new UserNotFoundException())
        .when(this.expenseService)
        .removeExpense(this.expense.getExpenseId());
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.removeExpense(this.expense.getExpenseId(), this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.USER_NOT_FOUND));
    verify(this.expenseService, times(1))
        .removeExpense(this.expense.getExpenseId());
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Remove expense tests: should have status 404 if expense not found")
  void remove_expense_not_found_expense_error_case() throws Exception {
    doThrow(new ExpenseNotFoundException())
        .when(this.expenseService)
        .removeExpense(this.expense.getExpenseId());
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.removeExpense(this.expense.getExpenseId(), this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EXPENSE_NOT_FOUND));
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Remove expense tests: should have status 403 if there is no token")
  void remove_expense_forbidden_error_case() throws Exception {
    doThrow(new ExpenseNotFoundException())
        .when(this.expenseService)
        .removeExpense(this.expense.getExpenseId());
    ResultActions response = this.removeExpense(this.expense.getExpenseId(), null);

    response.andExpect(status().isForbidden());
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(this.user.getUsername());
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Edit expense tests: should have status 200 and return edited expense")
  void edit_expense_success_case() throws Exception {
    when(this.expenseService.editExpense(this.expense.getExpenseId(), this.expense))
        .thenReturn(this.expense);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);

    ResultActions response =
        this.editExpense(this.expense, this.expense.getExpenseId(), this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.expenseId").exists());
    verify(this.expenseService, times(1))
        .editExpense(this.expense.getExpenseId(), this.expense);
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Edit expense tests: should have status 404 if user not found")
  void edit_expense_not_found_user_error_case() throws Exception {
    doThrow(new UserNotFoundException())
        .when(this.expenseService)
        .editExpense(this.expense.getExpenseId(), this.expense);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);

    ResultActions response =
        this.editExpense(this.expense, this.expense.getExpenseId(), this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.USER_NOT_FOUND));
    verify(this.expenseService, times(1))
        .editExpense(this.expense.getExpenseId(), this.expense);
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Edit expense tests: should have status 404 if expense not found")
  void edit_expense_not_found_expense_error_case() throws Exception {
    doThrow(new ExpenseNotFoundException())
        .when(this.expenseService)
        .editExpense(this.expense.getExpenseId(), this.expense);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);

    ResultActions response =
        this.editExpense(this.expense, this.expense.getExpenseId(), this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EXPENSE_NOT_FOUND));
    verify(this.expenseService, times(1))
        .editExpense(this.expense.getExpenseId(), this.expense);
    verify(this.userDetailsService, times(1))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Edit expense tests: should have status 403 if there is no token")
  void edit_expense_forbidden_error_case() throws Exception {
    doThrow(new ExpenseNotFoundException())
        .when(this.expenseService)
        .editExpense(this.expense.getExpenseId(), this.expense);

    ResultActions response =
        this.editExpense(this.expense, this.expense.getExpenseId(), null);

    response.andExpect(status().isForbidden());
    verify(this.expenseService, times(0))
        .editExpense(this.expense.getExpenseId(), this.expense);
    verify(this.userDetailsService, times(0))
        .loadUserByUsername(this.user.getUsername());
  }

  @Test
  @DisplayName("Expense DTO validation tests: empty tag test")
  void empty_tag_test() throws Exception {
    this.expenseDto.setTag(null);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_TAG));
  }

  @Test
  @DisplayName("Expense DTO validation tests: empty method test")
  void empty_method_test() throws Exception {
    this.expenseDto.setMethod(null);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_METHOD));
  }

  @Test
  @DisplayName("Expense DTO validation tests: empty value test")
  void empty_value_test() throws Exception {
    this.expenseDto.setValue(null);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_VALUE));
  }

  @Test
  @DisplayName("Expense DTO validation tests: empty description test")
  void empty_description_test() throws Exception {
    this.expenseDto.setDescription(null);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_DESCRIPTION));
  }

  @Test
  @DisplayName("Expense DTO validation tests: empty currency test")
  void empty_currency_test() throws Exception {
    this.expenseDto.setCurrency(null);
    when(this.userDetailsService.loadUserByUsername(this.user.getUsername())).thenReturn(this.user);
    ResultActions response = this.addNewExpense(this.expenseDto, this.token);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value(ExceptionsMessages.EMPTY_CURRENCY));
  }

  private ResultActions editExpense(Expense payload, UUID expenseId, String tkn) throws Exception {
    if (tkn != null) {
      return this.mockMvc.perform(put("/expense/{expenseId}", expenseId)
          .header("Authorization", "Bearer " + tkn)
          .contentType(MediaType.APPLICATION_JSON)
          .content(new ObjectMapper().writeValueAsString(payload))
      );
    }

    return this.mockMvc.perform(put("/expense/{expenseId}", expenseId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload))
    );
  }

  private ResultActions removeExpense(UUID expenseId, String token) throws Exception {
    if (token != null) {
      return this.mockMvc.perform(delete("/expense/{expenseId}", expenseId)
          .header("Authorization", "Bearer " + token));
    }

    return this.mockMvc.perform(delete("/expense/{expenseId}", expenseId));
  }

  private ResultActions addNewExpense(ExpenseDto payload, String token) throws Exception {
    if (token != null) {
      return this.mockMvc.perform(post("/expense")
          .header("Authorization", "Bearer " + token)
          .contentType(MediaType.APPLICATION_JSON)
          .content(new ObjectMapper().writeValueAsString(payload))
      );
    }

    return this.mockMvc.perform(post("/expense")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload))
    );
  }
}
