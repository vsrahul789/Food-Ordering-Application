package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.AuthRequestDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.AuthResponseDTO;
import com.foodorderingsystem.FoodOrderingApplication.dto.UserRegisterDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.entity.enums.UserRole;
import com.foodorderingsystem.FoodOrderingApplication.exception.InvalidCredentialsException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UserAlreadyExistsException;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import com.foodorderingsystem.FoodOrderingApplication.security.JwtProvider;
import com.foodorderingsystem.FoodOrderingApplication.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private UserService userService;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void registerUser_shouldRegisterSuccessfully() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("Alice");
        dto.setEmail("alice@example.com");
        dto.setPassword("password");
        dto.setRole(UserRole.CUSTOMER);
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName(dto.getName());
        savedUser.setEmail(dto.getEmail());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(UserRole.CUSTOMER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("alice@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowIfEmailExists() {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setName("Bob");
        dto.setEmail("bob@example.com");
        dto.setPassword("password");
        dto.setRole(null);
        when(userRepository.findByEmail(dto.getEmail()))
                .thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(dto));
    }

    @Test
    void loginUser_shouldLoginSuccessfully() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("john@example.com");
        dto.setPassword("securepass");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword("hashedPass");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), "hashedPass")).thenReturn(true);
        when(jwtProvider.generateToken(user)).thenReturn("mocked-jwt-token");

        AuthResponseDTO response = userService.loginUser(dto);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
    }

    @Test
    void loginUser_shouldThrowIfUserNotFound() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("notfound@example.com");
        dto.setPassword("pass");
        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(dto));
    }

    @Test
    void loginUser_shouldThrowIfPasswordIncorrect() {
        AuthRequestDTO dto = new AuthRequestDTO();
        dto.setEmail("john@example.com");
        dto.setPassword("wrongpass");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword("correctpass");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.loginUser(dto));
    }
}
