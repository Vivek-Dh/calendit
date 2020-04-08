package com.postman.calendit.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.SlotDTO;

public interface BookingService {

  BookingDTO bookSlot(String slotId, String owner, String requestor);
  
  SlotDTO addSlot(String owner, LocalDate date, LocalTime time);

  SlotDTO removeSlot(String slotId, String owner);

  Set<SlotDTO> listSlots(String owner);

  BookingDTO removeBooking(String bookingId);

  List<BookingDTO> listBookings(String userId);

}
