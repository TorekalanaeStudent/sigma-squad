package com.sigma_squad.computify.controller;

import com.sigma_squad.computify.dto.ChatBotRequest;
import com.sigma_squad.computify.dto.ChatBotResponse;
import com.sigma_squad.computify.entity.User;
import com.sigma_squad.computify.service.ChatBotService;
import com.sigma_squad.computify.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final ChatBotService chatBotService;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<ChatBotResponse> sendMessage(
        @RequestBody ChatBotRequest request,
        Authentication authentication
    ) throws IOException, InterruptedException {
        // Get user ID from authentication
        Long userId = Long.parseLong(authentication.getName());

        // Fetch user from database to get name and email
        User user = userService.getUserById(userId);

        // Send message with user context
        ChatBotResponse response = chatBotService.sendMessage(
            userId,
            request.getMessage(),
            user.getName(),
            user.getEmail()
        );
        return ResponseEntity.ok(response);
    }
}
