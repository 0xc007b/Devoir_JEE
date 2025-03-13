package com.azonnoudo.florent.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AnnouncementController {

    @GetMapping("/announcements")
    public String getAnnouncements() {
        return "announcements/dashboard";
    }
}
