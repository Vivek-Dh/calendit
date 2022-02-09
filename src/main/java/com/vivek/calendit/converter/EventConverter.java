package com.vivek.calendit.converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import com.google.api.services.calendar.model.Event;
import com.vivek.calendit.dto.EventDTO;

@Component
public class EventConverter {
  public EventDTO convert(Event event) {
    EventDTO eventDTO = new EventDTO();
    eventDTO.setAttendees(event.getAttendees());
    eventDTO.setCreated(LocalDateTime.ofEpochSecond(event.getCreated().getValue()/1000, 0 , ZoneOffset.ofTotalSeconds(0)));
    eventDTO.setCreator(event.getCreator());
    eventDTO.setDescription(event.getDescription());
    eventDTO.setEnd(LocalDateTime.ofEpochSecond(event.getEnd().getDateTime().getValue()/1000, 0, ZoneOffset.ofTotalSeconds(0)));
    eventDTO.setStart(LocalDateTime.ofEpochSecond(event.getStart().getDateTime().getValue()/1000, 0, ZoneOffset.ofTotalSeconds(0)));
    eventDTO.setHtmlLink(event.getHtmlLink());
    eventDTO.setId(event.getId());
    return eventDTO;
  }
}
