package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.service.Seat;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;

@ThreadSafe
@Controller
public class TicketController {

    private final TicketService ticketService;
    private final SessionService sessionService;

    public TicketController(TicketService ticketService, SessionService sessionService) {
        this.ticketService = ticketService;
        this.sessionService = sessionService;
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        model.addAttribute("tickets", ticketService.findAll());
        return "tickets";
    }

    /*
    @GetMapping("/updateTicket/{ticketId}")
    public String formUpdateTicket(Model model, @PathVariable("ticketId") int id) {
        model.addAttribute("ses", service.findById(id).get());
        model.addAttribute("halls", hallService.findAll());
        return "updateTicket";
    }

    @PostMapping("/updateTicket")
    public String updateTicket(@ModelAttribute Ticket ticket) {
        service.update(ticket);
        return "redirect:/tickets";
    }
*/
    @GetMapping("/selectSession")
    public String addTicket(Model model) {
        model.addAttribute("filmSessions", sessionService.findAll());
        return "selectSession";
    }

    @GetMapping("/selectSeat")
    public String selectSeat(Model model, @ModelAttribute FilmSession session) {
        session = sessionService.findById(session.getId()).get();
        model.addAttribute("ticket", new Ticket(0, null, null, 1, 1));
        model.addAttribute("filmSessions", session);
        model.addAttribute("availableSeats", ticketService.getAvailableSeats(session));
        return "selectSeat";
    }

    @GetMapping("/customerDetails")
    public String createTicket(@ModelAttribute Seat seat) {
        return "redirect:/tickets";
    }
}