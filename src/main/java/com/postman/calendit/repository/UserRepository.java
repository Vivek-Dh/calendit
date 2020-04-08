package com.postman.calendit.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.postman.calendit.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
