package com.postman.calendit.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events.Delete;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.postman.calendit.model.Booking;

@Service
@SuppressWarnings("deprecation")
public class GoogleCalendarService {

  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static final String CALENDARID = "primary";

  private static final String APPLICATION = "Calendit";

  public String addGoogleCalendarEntry(String accessToken, Booking booking)
      throws GeneralSecurityException, IOException {
    // Build a new authorized API client service.
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION).build();
    return createEvent(service, booking);
  }

  public List<Event> getEventsList(String accessToken)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION).build();
    return getGoogleCalendarBookings(service);
  }

  public void removeEvent(String id, String accessToken)
      throws GeneralSecurityException, IOException {
    final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION).build();
    Delete dd = service.events().delete(CALENDARID, id);
    dd.execute();
  }

  private String createEvent(Calendar service, Booking booking) throws IOException {
    Event event = new Event().setSummary("Calendit Meeting").setLocation("Online")
        .setDescription(
            "Meeting between : " + booking.getRequestor() + " and " + booking.getOwner())
        .setId(booking.getId());

    ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
    System.out.println(booking.getSlot().getStart());
    System.out.println(booking.getSlot().getEnd());
    long epoch = booking.getSlot().getStart().atZone(zoneId).toEpochSecond() * 1000;
    long epoch1 = booking.getSlot().getEnd().atZone(zoneId).toEpochSecond() * 1000;

    DateTime startDateTime = new DateTime(epoch);// new
                                                 // DateTime("2020-05-28T09:00:00-07:00");
    EventDateTime start = new EventDateTime().setDateTime(startDateTime);
    event.setStart(start);

    DateTime endDateTime = new DateTime(epoch1);// ("2020-06-28T17:00:00-07:00");
    EventDateTime end = new EventDateTime().setDateTime(endDateTime);
    event.setEnd(end);

    EventAttendee[] attendees =
        new EventAttendee[] {new EventAttendee().setEmail(booking.getRequestor()),
            new EventAttendee().setEmail(booking.getOwner())};
    event.setAttendees(Arrays.asList(attendees));

    EventReminder[] reminderOverrides =
        new EventReminder[] {new EventReminder().setMethod("email").setMinutes(24 * 60),
            new EventReminder().setMethod("popup").setMinutes(10),};
    Event.Reminders reminders =
        new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
    event.setReminders(reminders);

    event = service.events().insert(CALENDARID, event).execute();
    System.out.printf("Event created: %s\n", event.getHtmlLink());
    return event.getHtmlLink();
  }

  private List<Event> getGoogleCalendarBookings(Calendar service) throws IOException {
    DateTime now = new DateTime(System.currentTimeMillis());
    Events events = service.events().list(CALENDARID).setMaxResults(20).setTimeMin(now)
        .setOrderBy("startTime").setSingleEvents(true).execute();
    return events.getItems();
  }
}
