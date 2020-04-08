package com.postman.calendit.dto;

import java.util.Set;
import com.postman.calendit.model.Slot;
import lombok.Data;

@Data
public class UserDTO {
  private String id;
  private String name;
  private Set<Slot> openSlots;
}
