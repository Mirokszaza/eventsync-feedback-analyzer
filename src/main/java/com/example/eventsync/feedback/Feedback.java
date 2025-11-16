package com.example.eventsync.feedback;

import com.example.eventsync.event.Event;
import com.example.eventsync.sentiment.Sentiment;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;

    @Column(nullable = false, length = 4000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sentiment sentiment = Sentiment.UNKNOWN;

    private Double confidence;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Sentiment getSentiment() {
        return sentiment;
    }
    public void setSentiment(Sentiment sentiment) {
        this.sentiment = sentiment;
    }

    public Double getConfidence() {
        return confidence;
    }
    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
