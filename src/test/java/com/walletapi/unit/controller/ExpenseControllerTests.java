package com.walletapi.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walletapi.configure.ApplicationConfig;
import com.walletapi.configure.WebSecurityConfig;
import com.walletapi.controller.ExpenseController;
import com.walletapi.domain.ExpenseDto;
import com.walletapi.jwt.JwtService;
import com.walletapi.model.Expense;
import com.walletapi.service.ExpenseService;
import com.walletapi.service.UserDetailsServiceImp;
import com.walletapi.util.ExpenseDataExample;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(ExpenseController.class)
@Import({WebSecurityConfig.class, ApplicationConfig.class, JwtService.class})
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Expenses controller tests:")
public class ExpenseControllerTests {
  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ExpenseService expenseService;

  @MockBean
  private UserDetailsServiceImp userDetailsService;

  private final ExpenseDto expenseDto = new ExpenseDto();
  private final Expense expense = new Expense();

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
  }

  @Test
  @DisplayName("Add new expense tests: should have status 201 and return a new expense")
  void add_new_expense_success_case() throws Exception {
    when(this.expenseService.addExpense(this.expenseDto)).thenReturn(this.expense);
    ResultActions response = this.addNewExpense(this.expenseDto);

    response.andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.expenseId").exists());
  }

  private ResultActions addNewExpense(ExpenseDto payload) throws Exception {
    return this.mockMvc.perform(post("/expense")
        .contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(payload))
    );
  }
}
