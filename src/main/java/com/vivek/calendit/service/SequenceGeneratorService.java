package com.vivek.calendit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vivek.calendit.model.User;
import com.vivek.calendit.repository.BookingRepository;
import com.vivek.calendit.repository.UserRepository;

@Service
public class SequenceGeneratorService {

  @Autowired
  UserRepository userRepository;
  
  @Autowired
  BookingRepository bookingRepository;
  
  public String getSlotId(String userId) {
    User user = userRepository.findById(userId).orElse(null);
    if(user==null) {
      return String.valueOf(1);
    }
    else {
      return String.valueOf(user.getOpenSlots().size() + 1);
    }
  }
  
  public String getBookingId(String userId) {
    return userId.replaceAll("[^a-v0-9]", "") + String.valueOf(bookingRepository.findByRequestor(userId).size() + 1);
  }
}
