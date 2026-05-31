package com.sigma_squad.computify.chatbot.service.impl;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import com.sigma_squad.computify.chatbot.service.IChatBotService;
import com.sigma_squad.computify.chatbot.dto.ChatBotResponse;
import com.sigma_squad.computify.computer.entity.Computer.ComputerStatus;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * ChatBotServiceImpl - Implementation of IChatBotService
 * Handles chatbot interactions with rate limiting and AI integration.
 */
@Service
@RequiredArgsConstructor
public class ChatBotServiceImpl implements IChatBotService {

    @Value("${chatbot.api-key:}")
    private String apiKey;

    private final ComputerRepository computerRepository;

    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openai/gpt-oss-120b:free";
    private static final int MAX_TOKENS = 10;
    private static final long REFILL_INTERVAL_MS = 60 * 1000;

    private final Map<Long, Integer> userTokens = Collections.synchronizedMap(new HashMap<>());
    private final Map<Long, Long> lastRefillTime = Collections.synchronizedMap(new HashMap<>());

    @Override
    public ChatBotResponse sendMessage(Long userId, String userMessage, String userName, String userEmail) throws IOException, InterruptedException {
        checkRateLimit(userId);
        recordMessageTimestamp(userId);

        long totalComputers = computerRepository.count();
        long availableComputers = computerRepository.countByStatus(ComputerStatus.AVAILABLE);
        long reservedComputers = computerRepository.countByStatus(ComputerStatus.RESERVED);
        long inUseComputers = computerRepository.countByStatus(ComputerStatus.IN_USE);

        String botReply = getAIResponse(userMessage, userName, userEmail, totalComputers, availableComputers, reservedComputers, inUseComputers);
        int remainingMessages = getRemainingMessages(userId);

        return new ChatBotResponse(botReply, remainingMessages);
    }

    private void initializeUser(Long userId) {
        userTokens.putIfAbsent(userId, MAX_TOKENS);
        lastRefillTime.putIfAbsent(userId, System.currentTimeMillis());
    }

    private void refillTokens(Long userId) {
        long now = System.currentTimeMillis();
        long last = lastRefillTime.getOrDefault(userId, now);
        long elapsed = now - last;

        int tokensToAdd = (int) (elapsed / REFILL_INTERVAL_MS);
        if (tokensToAdd > 0) {
            int current = userTokens.getOrDefault(userId, MAX_TOKENS);
            userTokens.put(userId, Math.min(MAX_TOKENS, current + tokensToAdd));
            lastRefillTime.put(userId, last + (tokensToAdd * REFILL_INTERVAL_MS));
        }
    }

    private void checkRateLimit(Long userId) {
        initializeUser(userId);
        refillTokens(userId);

        int tokens = userTokens.getOrDefault(userId, MAX_TOKENS);
        if (tokens <= 0) {
            throw new BusinessRuleException(
                "No messages remaining. You get 1 token back per minute, up to 10."
            );
        }
    }

    private void recordMessageTimestamp(Long userId) {
        initializeUser(userId);
        refillTokens(userId);
        int tokens = userTokens.getOrDefault(userId, MAX_TOKENS);
        userTokens.put(userId, Math.max(0, tokens - 1));
    }

    private int getRemainingMessages(Long userId) {
        initializeUser(userId);
        refillTokens(userId);
        return userTokens.getOrDefault(userId, MAX_TOKENS);
    }

    private String buildSystemPrompt(String userName, String userEmail, long total, long available, long reserved, long inUse) {
        return """
            You are a helpful CLASS Assistant for the Computer Library Access Supported System 
            at National University.

            Your name is NOVA (NU. Online Virtual Assistant). You assist students and librarians with computer reservations,
            usage policies, and general questions about the CLASS system.

            User Context:
            - Name: %s
            - Email: %s
            - Role: Student

            Current Computer Availability (live data):
            - Total Computers: %d
            - Available: %d
            - Reserved: %d
            - In Use: %d

            Your responsibilities:
            - Help users understand computer reservations and library policies
            - Answer questions about the CLASS system
            - Report current computer availability when asked
            - Provide friendly and concise assistance
            - If you don't know something, be honest about it

            System Rules:
            - Students can reserve 1 computer at a time
            - Reservations expire after 5 minutes if not confirmed
            - Librarians must confirm reservations
            - Tokens refill at 1 per minute up to a maximum of 10

            Your Rules:
            - Write in plain text only, no markdown or code formatting
            - Make your messages concise and to the point
            - Always respond helpfully and respectfully
            - If asked about something outside your knowledge, say: I'm not sure about that. Please contact library staff for assistance.
            """.formatted(userName, userEmail, total, available, reserved, inUse);
    }

    private String getAIResponse(String userMessage, String userName, String userEmail, long total, long available, long reserved, long inUse) throws IOException, InterruptedException {
        if (apiKey == null || apiKey.isEmpty()) {
            return "I'm temporarily unavailable. Please try again later.";
        }

        String systemPrompt = buildSystemPrompt(userName, userEmail, total, available, reserved, inUse);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("model", MODEL);

        ArrayNode messages = mapper.createArrayNode();

        ObjectNode systemMsg = mapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        ObjectNode userMsg = mapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        root.set("messages", messages);

        String json = mapper.writeValueAsString(root);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                System.out.println("API Error " + response.statusCode() + ": " + response.body());
                return "Sorry, I encountered an error processing your request.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }

    private String parseResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            if (root.has("error")) {
                return "AI error: " + root.path("error").path("message").asText();
            }

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                System.out.println("DEBUG RAW RESPONSE: " + responseBody);
                return "AI returned empty response.";
            }

            JsonNode content = choices.get(0).path("message").path("content");

            if (content.isMissingNode() || content.isNull()) {
                System.out.println("DEBUG RAW RESPONSE: " + responseBody);
                return "AI response missing content field.";
            }

            return content.asText();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("RAW RESPONSE: " + responseBody);
            return "Failed to parse AI response.";
        }
    }
}
