package com.postman.calendit;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class CalenditApplication {

  public static void main(String[] args) {
    SpringApplication.run(CalenditApplication.class, args);
  }

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public ReentrantReadWriteLock getLock() {
    return new ReentrantReadWriteLock();
  }

}
