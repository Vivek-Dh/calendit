package com.postman.calendit.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.postman.calendit.converter.EventConverter;
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.EventDTO;
import com.postman.calendit.dto.SlotDTO;
import com.postman.calendit.model.Booking;
import com.postman.calendit.model.Slot;
import com.postman.calendit.model.User;
import com.postman.calendit.repository.BookingRepository;
import com.postman.calendit.repository.UserRepository;
import com.postman.calendit.util.Constants;

@Service
public class CalenditBookingService implements BookingService {

  @Autowired
  BookingRepository bookingRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  ModelMapper modelMapper;

  @Autowired
  SequenceGeneratorService sequenceService;
  
  @Autowired
  GoogleCalendarService googleCalendarService;
  
  @Autowired
  EventConverter eventConverter;

  @Autowired
  ReentrantReadWriteLock lock;

  @Override
  public String bookSlot(String slotId, String owner, String requestor, String accessToken) throws GeneralSecurityException, IOException {
    User user = userRepository.findById(owner).orElse(null);
    Set<Slot> openSlots = user.getOpenSlots();
    Slot slotToBeBooked =
        openSlots.stream().filter(slot -> slot.getId().equals(slotId)).findFirst().orElse(null);
    if(slotToBeBooked==null)
      throw new IllegalArgumentException("Invalid slot");
    String bookingId = sequenceService.getBookingId(requestor);
    Booking booking = new Booking(bookingId, requestor, owner, slotToBeBooked);
    openSlots.remove(slotToBeBooked);
    String htmlLink = googleCalendarService.addGoogleCalendarEntry(accessToken, booking);
    userRepository.save(user);
    bookingRepository.save(booking);
    return htmlLink + Constants.BOOKING_ID + bookingId;
  }

  @Override
  public SlotDTO addSlot(String owner, LocalDate date, LocalTime time) {
    String slotId = sequenceService.getSlotId(owner);
    Slot slot =
        new Slot(slotId, LocalDateTime.of(date, time), LocalDateTime.of(date, time).plusHours(1));
    User user = userRepository.findById(owner).orElse(null);
    user.getOpenSlots().add(slot);
    userRepository.save(user);
    return modelMapper.map(slot, SlotDTO.class);
  }

  @Override
  public SlotDTO removeSlot(String slotId, String owner) {
    User user = userRepository.findById(owner).orElse(null);
    Slot slotToBeRemoved = user.getOpenSlots().stream().filter(slot -> slot.getId().equals(slotId))
        .findFirst().orElse(null);
    if(slotToBeRemoved==null)
      throw new IllegalArgumentException("Invalid slot");
    user.getOpenSlots().remove(slotToBeRemoved);
    userRepository.save(user);
    return modelMapper.map(slotToBeRemoved, SlotDTO.class);
  }

  @Override
  public Set<SlotDTO> listSlots(String owner) {
    User user = userRepository.findById(owner).orElse(null);
    return new LinkedHashSet<SlotDTO>(user.getOpenSlots().stream()
        .map(slot -> modelMapper.map(slot, SlotDTO.class))
        .sorted((s1, s2) -> s1.getStart().compareTo(s2.getStart())).collect(Collectors.toSet()));
  }

  @Override
  public BookingDTO removeBooking(String bookingId, String accessToken) throws GeneralSecurityException, IOException {
    Booking booking = bookingRepository.findById(bookingId).orElse(null);
    if(booking==null)
      throw new IllegalArgumentException("Invalid booking");
    googleCalendarService.removeEvent(bookingId, accessToken);
    bookingRepository.deleteById(bookingId);
    return modelMapper.map(booking, BookingDTO.class);
  }

  @Override
  public List<EventDTO> listBookings(String accessToken) throws GeneralSecurityException, IOException {
    return googleCalendarService.getEventsList(accessToken).stream().map(event -> eventConverter.convert(event)).collect(Collectors.toList());
  }

}
