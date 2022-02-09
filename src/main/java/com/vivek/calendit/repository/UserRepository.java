package com.vivek.calendit.repository;

import com.vivek.calendit.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
