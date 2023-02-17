package com.walletapi.repositories;

import com.walletapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * User repository.
 */
public interface UserRepository extends MongoRepository<User, String> {
  @Query("{ 'username' : ?0 }")
  User findByUsername(String username);
}
