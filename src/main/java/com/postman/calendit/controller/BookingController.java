package com.postman.calendit.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.postman.calendit.dto.BookingDTO;
import com.postman.calendit.dto.SlotDTO;
import com.postman.calendit.service.BookingService;
import com.postman.calendit.util.Constants;

@Controller
@RequestMapping("/booking")
public class BookingController {

  @Autowired
  BookingService bookingService;

  @ResponseBody
  @GetMapping("/addslot")
  public SlotDTO addSlot(
      @RequestParam("date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date,
      @RequestParam("time") @DateTimeFormat(pattern = "HH:mm") LocalTime time,
      @AuthenticationPrincipal OAuth2User principal) {
    return bookingService.addSlot(principal.getAttribute("email"), date, time);
  }

  @ResponseBody
  @GetMapping("/listslots")
  public Set<SlotDTO> listSlots(@RequestParam("owner") String owner) {
    return bookingService.listSlots(owner);
  }

  @ResponseBody
  @GetMapping("/bookslot")
  public String bookSlot(@RequestParam("slotId") String slotId,
      @RequestParam("owner") String owner, @AuthenticationPrincipal OAuth2User principal) {
    return Constants.BOOKING_CONFIRMED + bookingService.bookSlot(slotId, owner, principal.getAttribute("email"));
  }

  @ResponseBody
  @GetMapping("/removeslot")
  public String removeSlot(@RequestParam("slotId") String slotId) {
    return String.format(Constants.SLOT_REMOVED, bookingService.removeSlot(slotId, "vivek"));
  }

  @ResponseBody
  @GetMapping("/removebooking")
  public String removeBooking(@RequestParam("bookingId") String bookingId) {
    return String.format(Constants.BOOKING_REMOVED, bookingService.removeBooking(bookingId));
  }

  @ResponseBody
  @GetMapping("/listbookings")
  public List<BookingDTO> listBookings(@AuthenticationPrincipal OAuth2User principal) {
    return bookingService.listBookings(principal.getAttribute("email"));
  }

}
