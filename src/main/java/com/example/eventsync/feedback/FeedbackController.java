package com.example.eventsync.feedback;

import com.example.eventsync.event.Event;
import com.example.eventsync.event.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class FeedbackController {

    private final EventService events;
    private final FeedbackService feedbacks;

    public FeedbackController(EventService events, FeedbackService feedbacks) {
        this.events = events;
        this.feedbacks = feedbacks;
    }

    // DTO for submitting feedback
    public static class SubmitFeedbackRequest {
        public String text;
    }

    // POST /events/{eventId}/feedback
    @PostMapping("/events/{eventId}/feedback")
    @ResponseStatus(HttpStatus.CREATED)
    public Feedback submit(
            @PathVariable Long eventId,
            @RequestBody SubmitFeedbackRequest req) {

        if (req == null || req.text == null || req.text.isBlank()) {
            throw new IllegalArgumentException("Feedback text is required");
        }

        Event e = events.getOrThrow(eventId);
        return feedbacks.add(e, req.text.trim());
    }

    // GET /events/{eventId}/summary
    @GetMapping("/events/{eventId}/summary")
    public FeedbackService.EventSummary summary(@PathVariable Long eventId) {
        Event e = events.getOrThrow(eventId);
        return feedbacks.summarize(e);
    }
}
