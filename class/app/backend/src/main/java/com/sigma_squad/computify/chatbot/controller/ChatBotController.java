package com.sigma_squad.computify.chatbot.controller;

import com.sigma_squad.computify.chatbot.dto.ChatBotRequest;
import com.sigma_squad.computify.chatbot.dto.ChatBotResponse;
import com.sigma_squad.computify.auth.entity.User;
import com.sigma_squad.computify.chatbot.service.IChatBotService;
import com.sigma_squad.computify.auth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatBotController {

    private final IChatBotService chatBotService;
    private final IUserService userService;

    @PostMapping("/send")
    public ResponseEntity<ChatBotResponse> sendMessage(
        @RequestBody ChatBotRequest request,
        Authentication authentication
    ) throws IOException, InterruptedException {
        Long userId = Long.parseLong(authentication.getName());
        User user = userService.getUserById(userId);

        ChatBotResponse response = chatBotService.sendMessage(
            userId,
            request.message(),
            user.getName(),
            user.getEmail()
        );
        return ResponseEntity.ok(response);
    }
}
