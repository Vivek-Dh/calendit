package com.postman.calendit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.postman.calendit.configuration.mock.WithMockCustomUser;
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.EventDTO;
import com.postman.calendit.dto.SlotDTO;
import com.postman.calendit.service.CalenditBookingService;
import com.postman.calendit.util.AccessTokenUtil;
import com.postman.calendit.util.Constants;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTest {

  private static final ObjectMapper mapper = new ObjectMapper();

  @MockBean
  CalenditBookingService calenditBookingService;

  @MockBean
  OAuth2AuthorizedClientService clientService;

  @MockBean
  AccessTokenUtil accessTokenUtil;

  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    mapper.findAndRegisterModules();
    mapper.registerModule(new JavaTimeModule());
  }

  private static Logger log = LoggerFactory.getLogger(BookingControllerTest.class);

  @Test
  @WithMockCustomUser
  public void whenAddSlot_thenReturnAddedSlotForLoggedInUser() throws Exception {
    SlotDTO slot = new SlotDTO();
    slot.setEnd(LocalDateTime.now().plusHours(1));
    slot.setStart(LocalDateTime.now());
    slot.setId("slot1");
    Mockito.when(calenditBookingService.addSlot(Mockito.anyString(), Mockito.any(LocalDate.class),
        Mockito.any(LocalTime.class))).thenReturn(slot);
    MvcResult result = this.mockMvc.perform(get("/booking/addslot?date=23-05-2020&time=14:00"))
        .andExpect(status().isOk()).andReturn();
    SlotDTO actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<SlotDTO>() {});
    assertEquals(slot, actual);
  }
  
  @Test
  @WithMockCustomUser
  public void whenListSlots_thenReturnAvailableSlotsForRequestedUser() throws Exception {
    SlotDTO slotDTO1 = new SlotDTO();
    slotDTO1.setId("1");
    slotDTO1.setStart(LocalDateTime.now());
    slotDTO1.setEnd(LocalDateTime.now().plusHours(1));
    SlotDTO slotDTO2 = new SlotDTO();
    slotDTO2.setId("2");
    slotDTO2.setStart(LocalDateTime.now());
    slotDTO2.setEnd(LocalDateTime.now().plusHours(2));
    Set<SlotDTO> slots=  new HashSet<>();
    Mockito.when(calenditBookingService.listSlots(Mockito.anyString())).thenReturn(slots);
    MvcResult result = this.mockMvc.perform(get("/booking/listslots?owner=mockowner"))
        .andExpect(status().isOk()).andReturn();
    Set<SlotDTO> actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<Set<SlotDTO>>() {});
    assertEquals(slots, actual);
  }
  
  @Test
  @WithMockCustomUser
  public void whenBookSlot_thenReturnConfirmedBooking() throws Exception {
    String booking = "test booking";
    Mockito.when(accessTokenUtil.getAccessToken(Mockito.any(OAuth2AuthorizedClientService.class), Mockito.any(OAuth2AuthenticationToken.class))).thenReturn("mocktoken");
    Mockito.when(calenditBookingService.bookSlot(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(booking);
    MvcResult result = this.mockMvc.perform(get("/booking/bookslot?slotId=1&owner=mockowner"))
        .andExpect(status().isOk()).andReturn();
    String actual = result.getResponse().getContentAsString();
    assertEquals(Constants.BOOKING_CONFIRMED+booking, actual);
  }
  
  @Test
  @WithMockCustomUser
  public void whenRemoveSlot_thenReturnRemovedSlot() throws Exception {
    SlotDTO slotDTO1 = new SlotDTO();
    slotDTO1.setId("1");
    slotDTO1.setStart(LocalDateTime.now());
    slotDTO1.setEnd(LocalDateTime.now().plusHours(1));
    Mockito.when(calenditBookingService.removeSlot(Mockito.anyString(), Mockito.anyString())).thenReturn(slotDTO1);
    MvcResult result = this.mockMvc.perform(get("/booking/removeslot?slotId=1"))
        .andExpect(status().isOk()).andReturn();
    String actual = result.getResponse().getContentAsString();
    assertEquals(String.format(Constants.SLOT_REMOVED,slotDTO1), actual);
  }
  
  @Test
  @WithMockCustomUser
  public void whenRemoveBooking_thenReturnRemovedBooking() throws Exception {
    BookingDTO booking = new BookingDTO();
    booking.setId("1");
    booking.setOwner("mockowner");
    booking.setRequestor("mockrequestor");
    booking.setSlot(null);
    Mockito.when(accessTokenUtil.getAccessToken(Mockito.any(OAuth2AuthorizedClientService.class), Mockito.any(OAuth2AuthenticationToken.class))).thenReturn("mocktoken");
    Mockito.when(calenditBookingService.removeBooking(Mockito.anyString(), Mockito.anyString())).thenReturn(booking);
    MvcResult result = this.mockMvc.perform(get("/booking/removebooking?bookingId=1"))
        .andExpect(status().isOk()).andReturn();
    String actual = result.getResponse().getContentAsString();
    assertEquals(String.format(Constants.BOOKING_REMOVED,booking), actual);
  }
  
  @Test
  @WithMockCustomUser
  public void whenListSlots_thenReturnAvailableBookingsForLoggedInUser() throws Exception {
    EventDTO event1 = new EventDTO();
    event1.setId("1");
    EventDTO event2 = new EventDTO();
    event2.setId("2");
    List<EventDTO> events=  new ArrayList<>();
    events.add(event1);
    events.add(event2);
    Mockito.when(accessTokenUtil.getAccessToken(Mockito.any(OAuth2AuthorizedClientService.class), Mockito.any(OAuth2AuthenticationToken.class))).thenReturn("mocktoken");
    Mockito.when(calenditBookingService.listBookings(Mockito.anyString())).thenReturn(events);
    MvcResult result = this.mockMvc.perform(get("/booking/listbookings"))
        .andExpect(status().isOk()).andReturn();
    List<EventDTO> actual = mapper.readValue(result.getResponse().getContentAsString(),
        new TypeReference<List<EventDTO>>() {});
    assertEquals(events, actual);
  }
  
  @Test
  public void whenListSlotsWithoutAuthentication_thenReturn401Error() throws Exception {
    SlotDTO slotDTO1 = new SlotDTO();
    slotDTO1.setId("1");
    slotDTO1.setStart(LocalDateTime.now());
    slotDTO1.setEnd(LocalDateTime.now().plusHours(1));
    SlotDTO slotDTO2 = new SlotDTO();
    slotDTO2.setId("2");
    slotDTO2.setStart(LocalDateTime.now());
    slotDTO2.setEnd(LocalDateTime.now().plusHours(2));
    Set<SlotDTO> slots=  new HashSet<>();
    Mockito.when(calenditBookingService.listSlots(Mockito.anyString())).thenReturn(slots);
    MvcResult result = this.mockMvc.perform(get("/booking/listslots?owner=mockowner"))
        .andExpect(status().isUnauthorized()).andReturn();
  }
}
