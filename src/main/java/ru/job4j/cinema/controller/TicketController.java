package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.exceptions.DuplicateTicketFieldsSessionRowSeatException;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.Seat;
import ru.job4j.cinema.service.SessionService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.util.MenuHTMLGenerator;
import ru.job4j.cinema.util.SessionTypeHandler;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

import static ru.job4j.cinema.util.CustomerHandler.getCustomerOfCurrentSession;

@ThreadSafe
@Controller
public class TicketController {

    private static final String HTML_MENU_CODE_TO_INJECT_STRING = "htmlMenuCodeToInject";
    private final TicketService ticketService;
    private final SessionService sessionService;

    public TicketController(TicketService ticketService,
                            SessionService sessionService) {
        this.ticketService = ticketService;
        this.sessionService = sessionService;
    }

    @GetMapping("/tickets")
    public String tickets(Model model, HttpSession session) {
        model.addAttribute("tickets", ticketService.findAll());
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "tickets"));
        return "tickets";
    }

    @GetMapping("/sessionSelection")
    public String sessionSelection(Model model, HttpSession session) {
        if (SessionTypeHandler.getCurrentSessionType(session).equals("guest")) {
            return "loginCustomerPage";
        }
        List<FilmSession> filmSessionList = sessionService.findAllSessionsWithAvailableSeats();
        if (filmSessionList.isEmpty()) {
            return "redirect:/errorWhenSelectSessions";
        }
        model.addAttribute("filmSessions", filmSessionList);
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "sessionSelection";
    }

    @GetMapping("/errorWhenSelectSessions")
    public String errorWhenSelectSessions(Model model, HttpSession session) {
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "errorWhenSelectSessions";
    }

    @GetMapping("/addTicket")
    public String addTicket(Model model,
                            @ModelAttribute FilmSession currFilmSession,
                            HttpSession session) {
        currFilmSession = sessionService.findById(currFilmSession.getId()).get();
        session.setAttribute("currSession", currFilmSession);
        model.addAttribute("filmSession", currFilmSession);
        List<Seat> seatList = ticketService.getAvailableSeats(currFilmSession);
        HashMap<String, Seat> seatsMap = new HashMap<>();
        for (Seat seat : seatList) {
            seatsMap.put(seat.toString(), seat);
        }
        session.setAttribute("seatsMap", seatsMap);
        model.addAttribute("seats", seatList);
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "addTicket";
    }

    @PostMapping("/createTicket")
    public String createTicket(Model model,
                               @RequestParam("seatString") final String seatString,
                               HttpSession session) {
        Customer customer = getCustomerOfCurrentSession(session);
        HashMap<?, ?> seatsMap = new HashMap<>();
        try {
            if (session.getAttribute("seatsMap") != null) {
                seatsMap = (HashMap<?, ?>) session.getAttribute("seatsMap");
            }
            Seat seatSelected = (Seat) seatsMap.get(seatString);
            ticketService.add(
                    new Ticket(0,
                            (FilmSession) session.getAttribute("currSession"),
                            customer,
                            seatSelected.getRowNum(),
                            seatSelected.getSeatNum()));
        } catch (DuplicateTicketFieldsSessionRowSeatException e) {
            model.addAttribute("errorMessage", String.format(
                    "Ticket for selected seat <%s> has been already bought.",
                    seatString));
            return "redirect:/errorWhenCreateNewTicket";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error occurred while creating new ticket.");
            return "redirect:/errorWhenCreateNewTicket";
        }
        return "redirect:/customerTickets";
    }

    @GetMapping("/errorWhenCreateNewTicket")
    public String errorWhenCreateNewTicket(Model model, HttpSession session) {
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "errorWhenCreateNewTicket";
    }
}