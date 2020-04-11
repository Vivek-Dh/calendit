package com.postman.calendit.dto;

import com.postman.calendit.model.Slot;
import lombok.Data;

@Data
public class BookingDTO {
  private String id;
  private String requestor;
  private String owner;
  private Slot slot;
}
