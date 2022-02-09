package com.vivek.calendit.model;

import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "user")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class User {
  @Id
  private String id;
  private String name;
  private Set<Slot> openSlots;
}
