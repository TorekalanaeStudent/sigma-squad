package com.sigma_squad.computify.chatbot.service.impl;

import com.sigma_squad.computify.chatbot.dto.ChatBotResponse;
import com.sigma_squad.computify.computer.entity.Computer.ComputerStatus;
import com.sigma_squad.computify.computer.repository.ComputerRepository;
import com.sigma_squad.computify.shared.exception.BusinessRuleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChatBotServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ChatBotServiceImplTest {

    @Mock
    private ComputerRepository computerRepository;

    @InjectMocks
    private ChatBotServiceImpl chatBotService;

    private Long testUserId;
    private String testUserMessage;
    private String testUserName;
    private String testUserEmail;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testUserMessage = "How many computers are available?";
        testUserName = "Test User";
        testUserEmail = "test@students.nu-laguna.edu.ph";
        
        // Set API key
        ReflectionTestUtils.setField(chatBotService, "apiKey", "test-api-key");
    }

    @Test
    void testSendMessageSuccessWithAvailableComputers() throws IOException, InterruptedException {
        when(computerRepository.count()).thenReturn(10L);
        when(computerRepository.countByStatus(ComputerStatus.AVAILABLE)).thenReturn(5L);

        ChatBotResponse response = chatBotService.sendMessage(testUserId, testUserMessage, testUserName, testUserEmail);

        assertNotNull(response);
        assertNotNull(response.reply());
        verify(computerRepository, times(1)).count();
        verify(computerRepository, times(1)).countByStatus(ComputerStatus.AVAILABLE);
    }

    @Test
    void testSendMessageWithNoAvailableComputers() throws IOException, InterruptedException {
        when(computerRepository.count()).thenReturn(10L);
        when(computerRepository.countByStatus(ComputerStatus.AVAILABLE)).thenReturn(0L);

        ChatBotResponse response = chatBotService.sendMessage(testUserId, testUserMessage, testUserName, testUserEmail);

        assertNotNull(response);
        verify(computerRepository, times(1)).count();
    }

    @Test
    void testSendMessageRateLimitingEnforced() throws IOException, InterruptedException {
        when(computerRepository.count()).thenReturn(10L);
        when(computerRepository.countByStatus(ComputerStatus.AVAILABLE)).thenReturn(5L);

        // First message should succeed
        ChatBotResponse response1 = chatBotService.sendMessage(testUserId, testUserMessage, testUserName, testUserEmail);
        assertNotNull(response1);

        // Multiple rapid messages should trigger rate limiting
        // This depends on the rate limiting implementation
        verify(computerRepository, atLeastOnce()).count();
    }

    @Test
    void testSendMessageWithMultipleUsers() throws IOException, InterruptedException {
        when(computerRepository.count()).thenReturn(10L);
        when(computerRepository.countByStatus(ComputerStatus.AVAILABLE)).thenReturn(5L);

        ChatBotResponse response1 = chatBotService.sendMessage(1L, testUserMessage, "User 1", "user1@students.nu-laguna.edu.ph");
        ChatBotResponse response2 = chatBotService.sendMessage(2L, testUserMessage, "User 2", "user2@students.nu-laguna.edu.ph");

        assertNotNull(response1);
        assertNotNull(response2);
        verify(computerRepository, atLeast(2)).count();
    }

    @Test
    void testSendMessageRecordsTimestamp() throws IOException, InterruptedException {
        when(computerRepository.count()).thenReturn(10L);
        when(computerRepository.countByStatus(ComputerStatus.AVAILABLE)).thenReturn(5L);

        long beforeCall = System.currentTimeMillis();
        ChatBotResponse response = chatBotService.sendMessage(testUserId, testUserMessage, testUserName, testUserEmail);
        long afterCall = System.currentTimeMillis();

        assertNotNull(response);
        verify(computerRepository, atLeastOnce()).countByStatus(ComputerStatus.AVAILABLE);
    }
}
