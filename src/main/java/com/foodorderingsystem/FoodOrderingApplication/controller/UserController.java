package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.UserLoginDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.UserRegisterDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterDTO dto) {
        userService.registerUser(dto);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestBody UserLoginDTO dto) {
        User user = userService.loginUser(dto);
        return "Login successful, Welcome " + user.getName();
    }

    @GetMapping("/testauth")
    public String testAuth(){
        return "Authenticated";
    }
}
