import React, { useState, useRef, useEffect } from 'react';
import serviceApi, { type ChatMessage, type ChatBotResponse } from '../api/serviceApi';
import styles from '../styles/chatBot.module.css';

interface ChatBotProps {
  onClose: () => void;
}

const ChatBot: React.FC<ChatBotProps> = ({ onClose }) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [inputValue, setInputValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [remainingMessages, setRemainingMessages] = useState(10);
  const [error, setError] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = async () => {
    if (!inputValue.trim()) return;

    if (remainingMessages <= 0) {
      setError('You have reached your message limit. Please try again in 10 minutes.');
      return;
    }

    // Add user message to UI
    const userMessage: ChatMessage = {
      id: Date.now().toString(),
      sender: 'user',
      message: inputValue,
      timestamp: new Date(),
    };

    setMessages([...messages, userMessage]);
    setInputValue('');
    setLoading(true);
    setError(null);

    try {
      const response: ChatBotResponse = await serviceApi.chatbot.sendMessage(inputValue);

      // Add bot response
      const botMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'bot',
        message: response.reply,
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, botMessage]);
      setRemainingMessages(response.remainingMessages);
    } catch (err: any) {
      setError(err.message || 'Failed to send message');
      const errorMessage: ChatMessage = {
        id: (Date.now() + 1).toString(),
        sender: 'bot',
        message: 'Sorry, I encountered an error. Please try again.',
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className={styles['chatbot-overlay']}>
      <div className={styles['chatbot-container']}>
        <div className={styles['chatbot-header']}>
          <h3>NOVA</h3>
          <button className={styles['close-btn']} onClick={onClose}>
            ✕
          </button>
        </div>

        <div className={styles['chatbot-messages']}>
          {messages.length === 0 && (
            <div className={styles['welcome-message']}>
              <p>👋 Hello! I'm your CLASS Assistant.</p>
              <p>I can help you with questions about computer reservations, library policies, and more.</p>
              <p className={styles['rate-limit']}>
                📊 Messages remaining: {remainingMessages}/10 (resets every 10 minutes)
              </p>
            </div>
          )}

          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`${styles['message']} ${styles[msg.sender === 'user' ? 'user-message' : 'bot-message']}`}
            >
              <div className={styles['message-content']}>
                {msg.sender === 'bot' && <span className={styles['bot-icon']}>🤖</span>}
                <p>{msg.message}</p>
              </div>
            </div>
          ))}

          {error && (
            <div className={styles['error-message']}>
              <span>⚠️</span>
              <p>{error}</p>
            </div>
          )}

          {loading && (
            <div className={`${styles['message']} ${styles['bot-message']}`}>
              <div className={styles['message-content']}>
                <span className={styles['bot-icon']}>🤖</span>
                <div className={styles['typing-indicator']}>
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}

          <div ref={messagesEndRef} />
        </div>

        <div className={styles['chatbot-footer']}>
          <div className={styles['input-group']}>
            <textarea
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="Ask me anything..."
              disabled={loading || remainingMessages <= 0}
              className={styles['message-input']}
              rows={3}
            />
            <button
              onClick={handleSendMessage}
              disabled={loading || !inputValue.trim() || remainingMessages <= 0}
              className={styles['send-btn']}
            >
              {loading ? '...' : '📤'}
            </button>
          </div>
          <div className={styles['info-text']}>
            Messages remaining: {remainingMessages}/10
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatBot;
