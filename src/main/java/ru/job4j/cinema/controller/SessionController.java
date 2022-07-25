package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.util.MenuHTMLGenerator;
import ru.job4j.cinema.util.SessionTypeHandler;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@ThreadSafe
@Controller
public class SessionController {

    private static final String HTML_MENU_CODE_TO_INJECT_STRING = "htmlMenuCodeToInject";
    private final SessionService service;
    private final HallService hallService;
    private final TicketService ticketService;

    public SessionController(SessionService sessionService,
                             HallService hallService,
                             TicketService ticketService) {
        this.service = sessionService;
        this.hallService = hallService;
        this.ticketService = ticketService;
    }

    @GetMapping("/sessions")
    public String sessions(Model model, HttpSession session) {
        List<FilmSession> listOfSessions = service.findAll();
        model.addAttribute("sessions", listOfSessions);
        List<Integer> numOfAvailableSeats = new ArrayList<>();
        for (FilmSession ses : listOfSessions) {
            numOfAvailableSeats.add(ticketService.getAvailableSeats(ses).size());
        }
        model.addAttribute("availableSeats", numOfAvailableSeats);
        model.addAttribute("sessionType", SessionTypeHandler.getCurrentSessionType(session));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "sessions"));
        return "sessions";
    }

    @GetMapping("/updateSession/{sessionId}")
    public String formUpdateSession(Model model,
                                    @PathVariable("sessionId") int id,
                                    HttpSession session) {
        model.addAttribute("ses", service.findById(id).get());
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "sessions"));
        return "updateSession";
    }

    @PostMapping("/updateSession")
    public String updateSession(@ModelAttribute FilmSession filmSession) {
        service.update(filmSession);
        return "redirect:/sessions";
    }

    @GetMapping("/addSession")
    public String addSession(Model model, HttpSession session) {
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "sessions"));
        return "addSession";
    }

    @PostMapping("/createSession")
    public String createSession(@ModelAttribute FilmSession filmSession) {
        service.add(filmSession);
        return "redirect:/sessions";
    }
}