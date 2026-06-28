/*package com.icss.hr.service;

import com.icss.hr.model.IntentResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RouterService {

    private final ChatClient chatClient;

    public RouterService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public IntentResponse routeMessage(String userMessage) {
        return this.chatClient.prompt()
                .system("""
                    You are an expert triage router for an enterprise system. 
                    Analyze the user's message and determine the intent.
                    The possible intents are: 'HR', 'ACCOUNTING', or 'GENERAL'.
                    """)
                .user(userMessage)

                .call()
                .entity(IntentResponse.class); 
    }
}*/

package com.icss.hr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icss.hr.model.IntentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class RouterService {

    private static final Logger log = LoggerFactory.getLogger(RouterService.class);

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public RouterService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public IntentResponse routeMessage(String userMessage) {
        try {
            String rawJson = this.chatClient.prompt()
                    .system("""
                        You are an expert triage router for an enterprise system.
                        Analyze the user's message and determine the intent.
                        The possible intents are: 'HR', 'ACCOUNTING', or 'GENERAL'.

                        You MUST respond ONLY with a valid JSON object matching this structure:
                        {
                          "intent": "THE_INTENT",
                          "confidenceScore": 0.95
                        }
                        Do not include any markdown formatting, code blocks, or extra text. Only return the raw JSON object.
                        """)
                    .user(userMessage)
                    .call()
                    .content();

            String cleanedJson = rawJson == null ? "{}" : rawJson.replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            return objectMapper.readValue(cleanedJson, IntentResponse.class);
        } catch (Exception e) {
            log.error("Failed to route message '{}'", userMessage, e);
            return new IntentResponse("GENERAL", 0.0);
        }
    }
}