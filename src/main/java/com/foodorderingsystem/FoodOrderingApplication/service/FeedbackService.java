package com.foodorderingsystem.FoodOrderingApplication.service;

import com.foodorderingsystem.FoodOrderingApplication.dto.FeedbackDTO;
import com.foodorderingsystem.FoodOrderingApplication.entity.Feedback;

import java.util.List;

public interface FeedbackService {
    Feedback leaveFeedback(FeedbackDTO dto);
    List<Feedback> getFeedbacksByUsers();
}

