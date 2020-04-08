package com.postman.calendit.service;

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
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.SlotDTO;
import com.postman.calendit.model.Booking;
import com.postman.calendit.model.Slot;
import com.postman.calendit.model.User;
import com.postman.calendit.repository.BookingRepository;
import com.postman.calendit.repository.UserRepository;

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
  ReentrantReadWriteLock lock;

  @Override
  public BookingDTO bookSlot(String slotId, String owner, String requestor) {
    User user = userRepository.findById(owner).orElse(null);
    Set<Slot> openSlots = user.getOpenSlots();
    Slot slotToBeBooked =
        openSlots.stream().filter(slot -> slot.getId().equals(slotId)).findFirst().orElse(null);
    if(slotToBeBooked==null)
      throw new IllegalArgumentException("Invalid slot");
    String bookingId = sequenceService.getBookingId(requestor);
    Booking booking = new Booking(bookingId, requestor, owner, slotToBeBooked);
    openSlots.remove(slotToBeBooked);
    userRepository.save(user);
    return modelMapper.map(bookingRepository.save(booking), BookingDTO.class);
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
  public BookingDTO removeBooking(String bookingId) {
    Booking booking = bookingRepository.findById(bookingId).orElse(null);
    if(booking==null)
      throw new IllegalArgumentException("Invalid booking");
    bookingRepository.deleteById(bookingId);
    return modelMapper.map(booking, BookingDTO.class);
  }

  @Override
  public List<BookingDTO> listBookings(String userId) {
    List<Booking> bookings = bookingRepository.findByRequestor(userId);
    return bookings.stream().map(booking -> modelMapper.map(booking, BookingDTO.class))
        .sorted((b1, b2) -> b1.getSlot().getStart().compareTo(b1.getSlot().getStart()))
        .collect(Collectors.toList());
  }

}
