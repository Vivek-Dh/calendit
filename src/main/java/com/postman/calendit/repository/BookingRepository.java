package com.postman.calendit.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.postman.calendit.model.Booking;

public interface BookingRepository extends MongoRepository<Booking, String>{
  public List<Booking> findByRequestor(String requestor);
}
