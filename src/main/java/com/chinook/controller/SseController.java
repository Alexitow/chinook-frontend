package com.chinook.controller;

import com.chinook.service.SseEmitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {

    private final SseEmitterService sseService;

    public SseController(SseEmitterService sseService) {
        this.sseService = sseService;
    }

    @GetMapping("/events")
    public SseEmitter events() {
        return sseService.subscribe();
    }
}
