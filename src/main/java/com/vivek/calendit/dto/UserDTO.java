package com.vivek.calendit.dto;

import java.util.Set;
import com.vivek.calendit.model.Slot;
import lombok.Data;

@Data
public class UserDTO {
  private String id;
  private String name;
  private Set<Slot> openSlots;
}
