package com.walletapi.repositories;

import com.walletapi.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * User repository.
 */
public interface UserRepository extends MongoRepository<User, String> {
}
