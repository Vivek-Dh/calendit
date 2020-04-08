package com.postman.calendit.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "booking")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
  @Id
  private String id;
  private String requestor;
  private String owner;
  private Slot slot;
}
