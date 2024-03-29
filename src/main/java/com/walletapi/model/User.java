package com.walletapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * User model.
 */
@Document("users")
public class User implements UserDetails {
  @Id
  private String id;

  @Indexed(unique = true)
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private List<Expense> expenseList = new ArrayList<>();

  public String getId() {
    return id;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return new ArrayList<>();
  }

  public String getPassword() {
    return password;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<Expense> getExpensesList() {
    return expenseList;
  }

  public void addExpense(Expense expense) {
    this.expenseList.add(expense);
  }
}
