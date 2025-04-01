package com.foodorderingsystem.FoodOrderingApplication.dto;


import com.foodorderingsystem.FoodOrderingApplication.entity.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
    private String name;
    private String email;
    private String password;
    private UserRole role;
}

