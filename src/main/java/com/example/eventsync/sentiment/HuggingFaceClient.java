package com.example.eventsync.sentiment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HuggingFaceClient {

    private final WebClient client;
    private final Duration timeout;

    public HuggingFaceClient(
            WebClient.Builder builder,
            @Value("${eventsync.hf.model:cardiffnlp/twitter-roberta-base-sentiment-latest}") String model,
            @Value("${eventsync.hf.timeoutMs:6000}") long timeoutMs,
            @Value("${HUGGING_FACE_TOKEN:}") String token
    ) {

        this.timeout = Duration.ofMillis(timeoutMs);

        WebClient.Builder customBuilder = builder
                .baseUrl("https://api-inference.huggingface.co/models/" + model)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        if (token != null && !token.isEmpty()) {
            customBuilder.defaultHeader("Authorization", "Bearer " + token);
        }

        this.client = customBuilder.build();
    }

    public SentimentResult classify(String text) {

        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult(Sentiment.UNKNOWN, 0);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", text);

        Map<String, Object> options = new HashMap<>();
        options.put("wait_for_model", true);

        body.put("options", options);

        try {
            Object raw = client.post()
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .timeout(timeout)
                    .block();

            List<Map<String, Object>> candidates = extractCandidates(raw);

            Sentiment bestLabel = Sentiment.UNKNOWN;
            double bestScore = 0;

            for (Map<String, Object> c : candidates) {
                Object labelObj = c.get("label");
                Object scoreObj = c.get("score");

                if (labelObj == null || scoreObj == null) continue;

                String label = labelObj.toString().toLowerCase();
                double score = Double.parseDouble(scoreObj.toString());

                Sentiment mapped;
                if (label.contains("pos")) mapped = Sentiment.POSITIVE;
                else if (label.contains("neu")) mapped = Sentiment.NEUTRAL;
                else if (label.contains("neg")) mapped = Sentiment.NEGATIVE;
                else mapped = Sentiment.UNKNOWN;

                if (score > bestScore) {
                    bestScore = score;
                    bestLabel = mapped;
                }
            }

            return new SentimentResult(bestLabel, bestScore);

        } catch (Exception e) {
            return new SentimentResult(Sentiment.UNKNOWN, 0);
        }
    }

    private List<Map<String, Object>> extractCandidates(Object raw) {

        List<Map<String, Object>> empty = new ArrayList<>();

        if (!(raw instanceof List<?>)) {
            return empty;
        }

        List<?> outer = (List<?>) raw;

        if (outer.isEmpty()) {
            return empty;
        }

        Object first = outer.get(0);

        // Case: List<List<Map>>
        if (first instanceof List<?>) {
            List<?> inner = (List<?>) first;

            List<Map<String, Object>> result = new ArrayList<>();
            for (Object o : inner) {
                if (o instanceof Map<?, ?>) {
                    result.add((Map<String, Object>) o);
                }
            }
            return result;
        }

        // Case: List<Map>
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object o : outer) {
            if (o instanceof Map<?, ?>) {
                result.add((Map<String, Object>) o);
            }
        }

        return result;
    }
}
