package org.example.testmentorbackend.services.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.testmentorbackend.dto.AiAppendQuestionsRequestDto;
import org.example.testmentorbackend.dto.AiQuizGenerateRequestDto;
import org.example.testmentorbackend.dto.QuestionDto;
import org.example.testmentorbackend.dto.QuizzesDto;
import org.example.testmentorbackend.services.AiQuizService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiQuizServiceImpl implements AiQuizService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.model}")
    private String model;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public QuizzesDto generateQuiz(AiQuizGenerateRequestDto request) {
        try {
            validateRequest(request);

            String prompt = buildPrompt(request);

            try {
                String content = callModel(prompt);
                String cleanJson = extractJson(content);

                System.out.println("CLEAN JSON:");
                System.out.println(cleanJson);

                QuizzesDto quiz = objectMapper.readValue(cleanJson, QuizzesDto.class);
                validateGeneratedQuiz(quiz);
                return quiz;

            } catch (Exception firstParseError) {
                System.out.println("FIRST PARSE FAILED, RETRYING...");
                firstParseError.printStackTrace();

                String retryPrompt = prompt + """

                        IMPORTANT:
                        Your previous response was invalid JSON.
                        Return corrected VALID JSON only.
                        Escape all inner double quotes properly.
                        Do not include any extra text.
                        Do not use markdown.
                        Do not wrap JSON in triple backticks.
                        """;

                String retryContent = callModel(retryPrompt);
                String retryCleanJson = extractJson(retryContent);

                System.out.println("RETRY CLEAN JSON:");
                System.out.println(retryCleanJson);

                QuizzesDto retryQuiz = objectMapper.readValue(retryCleanJson, QuizzesDto.class);
                validateGeneratedQuiz(retryQuiz);
                return retryQuiz;
            }

        } catch (HttpStatusCodeException e) {
            System.out.println("OPENROUTER ERROR STATUS: " + e.getStatusCode());
            System.out.println("OPENROUTER ERROR BODY: " + e.getResponseBodyAsString());
            throw new RuntimeException("OpenRouter request failed: " + e.getStatusCode(), e);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI generation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<QuestionDto> generateAdditionalQuestions(
            AiAppendQuestionsRequestDto request,
            String existingQuizTitle,
            String existingQuizDescription
    ) {
        try {
            if (request == null) {
                throw new RuntimeException("Request is null");
            }

            int questionCount = request.getQuestionCount() != null ? request.getQuestionCount() : 3;
            String difficulty = request.getDifficulty() != null ? request.getDifficulty() : "Easy";
            String language = request.getLanguage() != null ? request.getLanguage() : "English";

            String source = (request.getSourceText() != null && !request.getSourceText().isBlank())
                    ? request.getSourceText()
                    : request.getTopic();

            if (source == null || source.isBlank()) {
                source = existingQuizTitle + ". " + (existingQuizDescription != null ? existingQuizDescription : "");
            }

            String prompt = """
                Generate additional questions for an EXISTING quiz.

                Existing quiz:
                title: %s
                description: %s

                Requirements:
                - Create exactly %d NEW questions
                - Do NOT repeat existing questions
                - difficulty: %s
                - language: %s
                - Use SINGLE_CHOICE questions only
                - Each question must have exactly 4 options
                - Exactly one option must be correct
                - Keep questions clear and suitable for students
                - Return ONLY raw valid JSON
                - Do not use markdown
                - Do not wrap JSON in ``` blocks
                - Do not add explanations before or after JSON

                JSON structure:
                {
                  "questions": [
                    {
                      "questionText": "string",
                      "questionType": "SINGLE_CHOICE",
                      "aiAnswer": "string",
                      "options": [
                        {"optionText": "string", "isCorrect": false},
                        {"optionText": "string", "isCorrect": true},
                        {"optionText": "string", "isCorrect": false},
                        {"optionText": "string", "isCorrect": false}
                      ]
                    }
                  ]
                }

                Material:
                %s
                """.formatted(
                    existingQuizTitle,
                    existingQuizDescription != null ? existingQuizDescription : "",
                    questionCount,
                    difficulty,
                    language,
                    source
            );

            String content = callModel(prompt);
            String cleanJson = extractJson(content);

            JsonNode root = objectMapper.readTree(cleanJson);
            JsonNode questionsNode = root.get("questions");

            if (questionsNode == null || !questionsNode.isArray() || questionsNode.isEmpty()) {
                throw new RuntimeException("AI did not return questions");
            }

            return objectMapper.readerForListOf(QuestionDto.class).readValue(questionsNode);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate additional questions: " + e.getMessage(), e);
        }
    }

    private String callModel(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("HTTP-Referer", "http://localhost:5173");
        headers.set("X-Title", "TestMentor");

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", """
                                        You are a quiz generator.
                                        Return ONLY valid JSON.
                                        Do not use markdown.
                                        Do not add explanations before or after JSON.
                                        Escape all quotes inside JSON strings properly.
                                        Every string value must be valid JSON.
                                        """
                        ),
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        System.out.println("OPENROUTER STATUS: " + response.getStatusCode());
        System.out.println("OPENROUTER BODY: " + response.getBody());

        if (response.getBody() == null || response.getBody().isBlank()) {
            throw new RuntimeException("OpenRouter returned empty response");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode choicesNode = root.path("choices");

        if (!choicesNode.isArray() || choicesNode.isEmpty()) {
            throw new RuntimeException("OpenRouter response does not contain choices");
        }

        String content = choicesNode.get(0)
                .path("message")
                .path("content")
                .asText()
                .trim();

        System.out.println("MODEL CONTENT:");
        System.out.println(content);

        return content;
    }

    private void validateRequest(AiQuizGenerateRequestDto request) {
        if (request == null) {
            throw new RuntimeException("Request is null");
        }

        boolean noTopic = request.getTopic() == null || request.getTopic().isBlank();
        boolean noText = request.getSourceText() == null || request.getSourceText().isBlank();

        if (noTopic && noText) {
            throw new RuntimeException("Topic or sourceText must be provided");
        }
    }

    private String extractJson(String content) {
        if (content == null || content.isBlank()) {
            throw new RuntimeException("Model returned empty content");
        }

        content = content.trim();

        if (content.startsWith("```")) {
            content = content.replace("```json", "").replace("```", "").trim();
        }

        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");

        if (start == -1 || end == -1 || end <= start) {
            throw new RuntimeException("Model did not return valid JSON. Raw content: " + content);
        }

        return content.substring(start, end + 1);
    }

    private void validateGeneratedQuiz(QuizzesDto quiz) {
        if (quiz == null) {
            throw new RuntimeException("Generated quiz is null");
        }

        if (quiz.getTitle() == null || quiz.getTitle().isBlank()) {
            throw new RuntimeException("Generated quiz title is empty");
        }

        if (quiz.getQuestions() == null || quiz.getQuestions().isEmpty()) {
            throw new RuntimeException("Generated quiz has no questions");
        }
    }

    private String buildPrompt(AiQuizGenerateRequestDto request) {
        int questionCount = request.getQuestionCount() != null ? request.getQuestionCount() : 5;
        String difficulty = request.getDifficulty() != null ? request.getDifficulty() : "Easy";
        String language = request.getLanguage() != null ? request.getLanguage() : "English";

        String source = (request.getSourceText() != null && !request.getSourceText().isBlank())
                ? request.getSourceText()
                : request.getTopic();

        return """
                Generate a quiz based on the provided material.

                Requirements:
                - Create exactly %d questions
                - difficulty: %s
                - language: %s
                - Use SINGLE_CHOICE questions only
                - Each question must have exactly 4 options
                - Exactly one option must be correct
                - Keep questions clear and suitable for students
                - Return ONLY raw valid JSON
                - Do not use markdown
                - Do not wrap JSON in ``` blocks
                - Do not add explanations before or after JSON
                - Escape all quotation marks inside strings properly
                - Do not use unescaped double quotes inside questionText, aiAnswer, or optionText

                JSON structure:
                {
                  "title": "string",
                  "description": "string",
                  "requiredVotes": 6,
                  "questions": [
                    {
                      "questionText": "string",
                      "questionType": "SINGLE_CHOICE",
                      "aiAnswer": "string",
                      "options": [
                        {"optionText": "string", "isCorrect": false},
                        {"optionText": "string", "isCorrect": true},
                        {"optionText": "string", "isCorrect": false},
                        {"optionText": "string", "isCorrect": false}
                      ]
                    }
                  ]
                }

                Material:
                %s
                """.formatted(questionCount, difficulty, language, source);
    }
}