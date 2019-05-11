package com.example.api.dto;

import com.example.foodorder.common.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtAuthResponseDto {

  private String token;
  private User user;

}
