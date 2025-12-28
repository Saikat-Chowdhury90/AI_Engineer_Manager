package com.example.aiem.controller;

import com.example.aiem.dto.StandupResponse;
import com.example.aiem.service.StandupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class HealthController {

    private final StandupService standupService;

    public HealthController(StandupService standupService) {
        this.standupService = standupService;
    }

    @GetMapping("/health")
    public String health(){
        return "ok";
    }

    @GetMapping("/api/standup")
    public StandupResponse standup(){
       return standupService.generateStandup();
    }

}
