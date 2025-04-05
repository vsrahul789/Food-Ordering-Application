package com.foodorderingsystem.FoodOrderingApplication.service.impl;

import com.foodorderingsystem.FoodOrderingApplication.dto.FeedbackDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Feedback;
import com.foodorderingsystem.FoodOrderingApplication.entity.User;
import com.foodorderingsystem.FoodOrderingApplication.exception.BadRequestException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UnauthorizedAccessException;
import com.foodorderingsystem.FoodOrderingApplication.exception.UserNotFoundException;
import com.foodorderingsystem.FoodOrderingApplication.repository.FeedbackRepository;
import com.foodorderingsystem.FoodOrderingApplication.repository.UserRepository;
import com.foodorderingsystem.FoodOrderingApplication.service.FeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedAccessException("User is not authenticated");
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        }

        throw new UnauthorizedAccessException("Invalid authentication principal");
    }

    @Override
    public Feedback leaveFeedback(FeedbackDTO dto) {
        User user = getAuthenticatedUser();

        if (dto.getStars() < 1 || dto.getStars() > 5) {
            throw new BadRequestException("Star rating must be between 1 and 5");
        }

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setMessage(dto.getMessage());
        feedback.setTimestamp(LocalDateTime.now());
        feedback.setStars(dto.getStars());

        log.info("User {} submitted feedback: {} ({} stars)", user.getEmail(), dto.getMessage(), dto.getStars());
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getFeedbacksByUser() {
        User user = getAuthenticatedUser();
        log.info("Fetching feedbacks for user: {}", user.getEmail());
        return feedbackRepository.findAll();
    }
}
