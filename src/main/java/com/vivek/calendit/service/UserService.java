package com.vivek.calendit.service;

import java.util.TreeSet;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vivek.calendit.dto.UserDTO;
import com.vivek.calendit.model.Slot;
import com.vivek.calendit.model.User;
import com.vivek.calendit.repository.UserRepository;

@Service
public class UserService {
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  ModelMapper modelMapper;
  
  public boolean checkIfUserExists(String id) {
    return userRepository.findById(id).isPresent();
  }
  
  public UserDTO registerUser(String id, String name) {
    User user = new User(id, name, new TreeSet<Slot>());
    return modelMapper.map(userRepository.save(user), UserDTO.class);
  }
}
