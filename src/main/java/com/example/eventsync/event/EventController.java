package com.example.eventsync.event;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    // DTO for create event request
    public static class CreateEventRequest {
        public String title;
        public String description;
    }

    // POST /events
    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Event create(@RequestBody CreateEventRequest req) {
        if (req == null || req.title == null || req.title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        return service.create(req.title.trim(),
                req.description == null ? "" : req.description.trim());
    }

    // GET /events
    @GetMapping("/events")
    public List<Event> list() {
        return service.list();
    }
}
