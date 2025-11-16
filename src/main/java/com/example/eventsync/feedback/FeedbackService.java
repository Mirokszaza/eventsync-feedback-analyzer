package com.example.eventsync.feedback;

import com.example.eventsync.event.Event;
import com.example.eventsync.sentiment.HuggingFaceClient;
import com.example.eventsync.sentiment.Sentiment;
import com.example.eventsync.sentiment.SentimentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository repo;
    private final HuggingFaceClient hf;

    public FeedbackService(FeedbackRepository repo, HuggingFaceClient hf) {
        this.repo = repo;
        this.hf = hf;
    }

    public Feedback add(Event event, String text) {
        SentimentResult result = hf.classify(text);
        Feedback f = new Feedback();
        f.setEvent(event);
        f.setText(text);
        f.setSentiment(result.label());
        f.setConfidence(result.confidence());
        return repo.save(f);
    }

    public static class EventSummary {
        public Long eventId;
        public String title;
        public long total;
        public long positive;
        public long neutral;
        public long negative;
        public double positivePct;
        public double neutralPct;
        public double negativePct;
    }

    public EventSummary summarize(Event event) {
        long total = repo.countByEvent(event);
        long pos = repo.countByEventAndSentiment(event, Sentiment.POSITIVE);
        long neu = repo.countByEventAndSentiment(event, Sentiment.NEUTRAL);
        long neg = repo.countByEventAndSentiment(event, Sentiment.NEGATIVE);

        EventSummary s = new EventSummary();
        s.eventId = event.getId();
        s.title = event.getTitle();
        s.total = total;
        s.positive = pos;
        s.neutral = neu;
        s.negative = neg;

        if (total == 0) {
            s.positivePct = s.neutralPct = s.negativePct = 0;
        } else {
            s.positivePct = pos * 100.0 / total;
            s.neutralPct = neu * 100.0 / total;
            s.negativePct = neg * 100.0 / total;
        }

        return s;
    }
}
