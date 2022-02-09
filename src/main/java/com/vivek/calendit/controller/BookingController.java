package com.vivek.calendit.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.vivek.calendit.dto.EventDTO;
import com.vivek.calendit.dto.SlotDTO;
import com.vivek.calendit.service.BookingService;
import com.vivek.calendit.util.AccessTokenUtil;
import com.vivek.calendit.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/booking")
public class BookingController {

  @Autowired
  BookingService bookingService;
  
  @Autowired
  OAuth2AuthorizedClientService clientService;
  
  @Autowired
  AccessTokenUtil accessTokenUtil;

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
  public String bookSlot(@RequestParam("slotId") String slotId, @RequestParam("owner") String owner,
      @AuthenticationPrincipal OAuth2User principal, OAuth2AuthenticationToken oauthToken) throws GeneralSecurityException, IOException {
    String accessToken = accessTokenUtil.getAccessToken(clientService, oauthToken);
    return Constants.BOOKING_CONFIRMED
        + bookingService.bookSlot(slotId, owner, principal.getAttribute("email"), accessToken);
  }

  @ResponseBody
  @GetMapping("/removeslot")
  public String removeSlot(@RequestParam("slotId") String slotId, @AuthenticationPrincipal OAuth2User principal) {
    return String.format(Constants.SLOT_REMOVED, bookingService.removeSlot(slotId, principal.getAttribute("email") ));
  }

  @ResponseBody
  @GetMapping("/removebooking")
  public String removeBooking(@RequestParam("bookingId") String bookingId, OAuth2AuthenticationToken oauthToken) throws GeneralSecurityException, IOException {
    String accessToken = accessTokenUtil.getAccessToken(clientService, oauthToken);
    return String.format(Constants.BOOKING_REMOVED, bookingService.removeBooking(bookingId, accessToken));
  }

  @ResponseBody
  @GetMapping("/listbookings")
  public List<EventDTO> listBookings(OAuth2AuthenticationToken oauthToken) throws GeneralSecurityException, IOException {
    String accessToken = accessTokenUtil.getAccessToken(clientService, oauthToken);
    return bookingService.listBookings(accessToken);
  }

}
