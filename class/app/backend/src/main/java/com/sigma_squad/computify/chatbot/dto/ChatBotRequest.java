package com.sigma_squad.computify.chatbot.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * ChatBotRequest - DTO for chatbot message request
 */
public record ChatBotRequest(
    @NotBlank(message = "Message is required")
    String message
) {}
