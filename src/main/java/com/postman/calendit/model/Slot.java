package com.postman.calendit.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Slot {
  @Id
  private String id;
  private LocalDateTime start;
  private LocalDateTime end;
}
