# calendit
Calendly like backend built with Spring Boot and Java that integrates with Google sign in and calendar.

Application URL : http://calendit.herokuapp.com/

Sign in on the homepage and use the below listed apis to create bookings on Google calendar.

1) /booking/addslot?date="dd-MM-yyyy"&time="HH:mm"
2) /booking/listslots?owner="email id of person (you can give yours too) whose slots you want to see, must be registered"
3) /booking/bookslot?slotId="choose a slot id from listslots endpoint"&owner="email id of owner"
4) /booking/removeslot?slotId="choose your slot id to remove from availability, logged in user"
5) /booking/removebooking?bookingId="booking id to be removed"
6) /booking/listbookings
