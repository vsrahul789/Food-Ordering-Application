package com.foodorderingsystem.FoodOrderingApplication.service;


import com.foodorderingsystem.FoodOrderingApplication.dto.UserLoginDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.UserRegisterDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;

public interface UserService {
    User registerUser(UserRegisterDTO dto);
    User loginUser(UserLoginDTO dto);
}

