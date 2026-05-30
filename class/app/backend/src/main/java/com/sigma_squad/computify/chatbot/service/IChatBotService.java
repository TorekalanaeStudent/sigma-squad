package com.sigma_squad.computify.chatbot.service;

import com.sigma_squad.computify.chatbot.dto.ChatBotResponse;

import java.io.IOException;

/**
 * IChatBotService - Contract for chatbot operations
 */
public interface IChatBotService {
    ChatBotResponse sendMessage(Long userId, String userMessage, String userName, String userEmail) throws IOException, InterruptedException;
}
