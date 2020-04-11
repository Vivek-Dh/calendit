package com.postman.calendit.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.EventDTO;
import com.postman.calendit.dto.SlotDTO;

public interface BookingService {

  String bookSlot(String slotId, String owner, String requestor, String accessToken) throws GeneralSecurityException, IOException;
  
  SlotDTO addSlot(String owner, LocalDate date, LocalTime time);

  SlotDTO removeSlot(String slotId, String owner);

  Set<SlotDTO> listSlots(String owner);

  BookingDTO removeBooking(String bookingId, String accessToken) throws GeneralSecurityException, IOException;

  List<EventDTO> listBookings(String userId) throws GeneralSecurityException, IOException;

}
