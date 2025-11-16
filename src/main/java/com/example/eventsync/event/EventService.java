package com.example.eventsync.event;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventService {

    private final EventRepository repo;

    public EventService(EventRepository repo) {
        this.repo = repo;
    }

    public Event create(String title, String description) {
        Event e = new Event();
        e.setTitle(title);
        e.setDescription(description == null ? "" : description);
        return repo.save(e);
    }

    public List<Event> list() {
        return repo.findAll();
    }

    public Event getOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
    }
}
