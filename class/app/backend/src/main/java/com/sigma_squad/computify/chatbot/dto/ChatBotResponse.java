package com.sigma_squad.computify.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ChatBotResponse - DTO for chatbot message response
 */
public record ChatBotResponse(
    @NotBlank(message = "Reply is required")
    String reply,
    
    int remainingMessages
) {}
