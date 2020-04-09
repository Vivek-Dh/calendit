package com.postman.calendit.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.EventAttendee;
import lombok.Data;

@Data
public class EventDTO {
  private List<EventAttendee> attendees;
  private LocalDateTime created;
  private Creator creator;
  private String description;
  private LocalDateTime end;
  private LocalDateTime start;
  private String htmlLink;
  private String id;
}
