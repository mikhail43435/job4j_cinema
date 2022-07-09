package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Session;
import ru.job4j.cinema.service.SessionService;

@ThreadSafe
@Controller
public class SessionController {

    private final SessionService service;
    private final SessionService hallService;

    public SessionController(SessionService sessionService, SessionService hallService) {
        this.service = sessionService;
        this.hallService = hallService;
    }

    @GetMapping("/sessions")
    public String sessions(Model model) {
        model.addAttribute("sessions", service.findAll());
        return "sessions";
    }

    @GetMapping("/updateSession/{sessionId}")
    public String formUpdateSession(Model model, @PathVariable("sessionId") int id) {
        model.addAttribute("ses", service.findById(id).get());
        model.addAttribute("halls", hallService.findAll());
        return "updateSession";
    }

    @PostMapping("/updateSession")
    public String updateSession(@ModelAttribute Session session) {
        service.update(session);
        return "redirect:/sessions";
    }

    @GetMapping("/addSession")
    public String addSession(Model model) {
        model.addAttribute("session", new Session(0, "New session", null));
        model.addAttribute("halls", hallService.findAll());
        return "addSession";
    }

    @PostMapping("/createSession")
    public String createSession(@ModelAttribute Session session) {
        service.add(session);
        return "redirect:/sessions";
    }

/*
    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model) {
        return "redirect:/errorWhenCreateNewSession";
    }
*/

/*    @GetMapping("/errorWhenCreateNewSession")
    public String errorWhenCreateNewSession(Model model) {
        //model.addAttribute("session", new Session(0, "New session name", 1, 1));
        return "/errorWhenCreateNewSession";
    }*/
}