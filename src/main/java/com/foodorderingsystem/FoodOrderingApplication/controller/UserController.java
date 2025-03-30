package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.AuthRequestDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.AuthResponseDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.UserRegisterDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.security.JwtProvider;
import com.foodorderingsystem.FoodOrderingApplication.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    public UserController(UserService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRegisterDTO dto) {
        userService.registerUser(dto);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO dto) {
        User user = userService.loginUser(dto);
        String token = jwtProvider.generateToken(user);
        return new AuthResponseDTO(token);
    }

    @GetMapping("/testauth")
    public String testAuth(){
        return "Authenticated";
    }
}
