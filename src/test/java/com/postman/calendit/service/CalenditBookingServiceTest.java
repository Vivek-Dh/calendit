package com.postman.calendit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.google.api.services.calendar.model.Event;
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

@SpringBootTest
public class CalenditBookingServiceTest {
  
  @Autowired
  CalenditBookingService calenditBookingService;
  
  @MockBean
  BookingRepository bookingRepository;

  @MockBean
  UserRepository userRepository;
  
  @Autowired
  ModelMapper modelMapper;

  @MockBean
  SequenceGeneratorService sequenceService;
  
  @MockBean
  GoogleCalendarService googleCalendarService;
  
  @MockBean
  EventConverter eventConverter;
  
  @Test
  public void whenBookSlot_thenReturnBookingDetails() throws GeneralSecurityException, IOException {
    Set<Slot> slots = new HashSet<Slot>();
    Slot slot1 = new Slot("1", LocalDateTime.now(), LocalDateTime.now().plusHours(1));
    Slot slot2 = new Slot("2", LocalDateTime.now(), LocalDateTime.now().plusHours(2));
    slots.add(slot1);
    slots.add(slot2);
    User user = new User(null, null, slots);
    Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
    Mockito.when(sequenceService.getBookingId(Mockito.anyString())).thenReturn("booking1");
    Mockito.when(googleCalendarService.addGoogleCalendarEntry(Mockito.anyString(), Mockito.any(Booking.class))).thenReturn("bookinglink");
    Mockito.doReturn(null).when(userRepository).save(Mockito.any(User.class));
    Mockito.doReturn(null).when(bookingRepository).save(Mockito.any(Booking.class));
    String actual = calenditBookingService.bookSlot("1","mockowner","mockrequestor","mocktoken");
    assertEquals("bookinglink" + Constants.BOOKING_ID + "booking1", actual);
  }
  
  @Test
  public void whenAddSlot_thenReturnAddedSlot() {
    SlotDTO slot = new SlotDTO();
    slot.setId("slot1");
    slot.setStart(LocalDateTime.now());
    slot.setEnd(LocalDateTime.now().plusHours(1));
    Mockito.when(sequenceService.getSlotId(Mockito.anyString())).thenReturn("slot1");
    User user = new User(null, null, new HashSet<Slot>());
    Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
    Mockito.doReturn(null).when(userRepository).save(Mockito.any(User.class));
    SlotDTO actual = calenditBookingService.addSlot("mockowner", LocalDate.now(), slot.getStart().toLocalTime());
    assertEquals(slot, actual);
    assertEquals(user.getOpenSlots().size(), 1);
  }
  
  @Test
  public void whenRemoveSlot_thenReturnRemovedSlot() {
    Set<Slot> slots = new HashSet<Slot>();
    Slot slot = new Slot("slot1",LocalDateTime.now(),LocalDateTime.now().plusHours(1));
    slots.add(slot);
    User user = new User(null, null, slots);
    Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
    Mockito.doReturn(null).when(userRepository).save(Mockito.any(User.class));
    SlotDTO expected = modelMapper.map(slot, SlotDTO.class);
    SlotDTO actual = calenditBookingService.removeSlot("slot1", "mockowner");
    assertEquals(expected, actual);
  }
  
  @Test
  public void whenListSlots_thenReturnAvailableSlots() {
    Set<Slot> slots = new HashSet<Slot>();
    Slot slot = new Slot("slot1",LocalDateTime.now(),LocalDateTime.now().plusHours(1));
    slots.add(slot);
    User user = new User(null, null, slots);
    Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
    Set<SlotDTO> expected = new LinkedHashSet<SlotDTO>(slots.stream()
        .map(slot1 -> modelMapper.map(slot1, SlotDTO.class))
        .sorted((s1, s2) -> s1.getStart().compareTo(s2.getStart())).collect(Collectors.toSet()));
    Set<SlotDTO> actual = calenditBookingService.listSlots("mockowner");
    assertEquals(expected, actual);
  }
  
  @Test
  public void whenRemoveBooking_thenReturnRemovedBooking() throws GeneralSecurityException, IOException {
    Booking booking = new Booking("booking1", null, null, null);
    BookingDTO expected = modelMapper.map(booking, BookingDTO.class);
    Mockito.when(bookingRepository.findById(Mockito.anyString())).thenReturn(Optional.of(booking));
    Mockito.doNothing().when(googleCalendarService).removeEvent(Mockito.anyString(), Mockito.anyString());
    Mockito.doNothing().when(bookingRepository).deleteById(Mockito.anyString());
    BookingDTO actual = calenditBookingService.removeBooking("booking1", "mocktoken");
    assertEquals(expected, actual);
  }
  
  @Test
  public void whenListBookings_thenReturnBookings() throws GeneralSecurityException, IOException {
    Event event1 = new Event();
    event1.setId("e1");
    event1.setHtmlLink("l1");
    Event event2 = new Event();
    event2.setId("e2");
    event2.setHtmlLink("l2");
    List<Event> events = new ArrayList<Event>();
    List<EventDTO> expected = events.stream().map(event -> eventConverter.convert(event)).collect(Collectors.toList());
    Mockito.when(googleCalendarService.getEventsList(Mockito.anyString())).thenReturn(events);
    List<EventDTO> actual = calenditBookingService.listBookings("mocktoken");
    assertEquals(expected, actual);
  }
}
