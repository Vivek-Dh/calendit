package com.vivek.calendit.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SlotDTO {
  private String id;
  private LocalDateTime start;
  private LocalDateTime end;
}
