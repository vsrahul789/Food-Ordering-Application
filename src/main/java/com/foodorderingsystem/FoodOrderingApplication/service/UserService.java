package com.foodorderingsystem.FoodOrderingApplication.service;


import com.foodorderingsystem.FoodOrderingApplication.dto.AuthRequestDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.UserRegisterDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;

public interface UserService {
    User registerUser(UserRegisterDTO dto);
    User loginUser(AuthRequestDTO dto);
}

