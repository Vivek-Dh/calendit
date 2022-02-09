package com.vivek.calendit.repository;

import java.util.List;

import com.vivek.calendit.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String>{
  public List<Booking> findByRequestor(String requestor);
}
