package com.example.eventsync.feedback;

import com.example.eventsync.event.Event;
import com.example.eventsync.sentiment.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    long countByEvent(Event event);

    long countByEventAndSentiment(Event event, Sentiment sentiment);
}
