package com.foodorderingsystem.FoodOrderingApplication.controller;

import com.foodorderingsystem.FoodOrderingApplication.dto.FeedbackDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Feedback;
import com.foodorderingsystem.FoodOrderingApplication.service.FeedbackService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<String> leaveFeedback(@RequestBody FeedbackDTO dto) {
        feedbackService.leaveFeedback(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Feedback submitted successfully");
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getFeedbacks() {
        return ResponseEntity.ok(feedbackService.getFeedbacksByUsers());
    }
}
