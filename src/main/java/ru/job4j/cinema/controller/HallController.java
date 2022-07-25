package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.HallService;
import ru.job4j.cinema.service.LoggerService;
import ru.job4j.cinema.util.MenuHTMLGenerator;

import javax.servlet.http.HttpSession;

@ThreadSafe
@Controller
public class HallController {

    private static final String HTML_MENU_CODE_TO_INJECT_STRING = "htmlMenuCodeToInject";
    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping("/halls")
    public String halls(Model model, HttpSession session) {
        model.addAttribute("halls", hallService.findAll());
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "halls"));
        return "halls";
    }

    @GetMapping("/updateHall/{hallId}")
    public String formUpdateHall(Model model, HttpSession session, @PathVariable("hallId") int id) {
        model.addAttribute("hall", hallService.findById(id));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "updateHall";
    }

    @PostMapping("/updateHall")
    public String updateHall(@ModelAttribute Hall hall) {
        hallService.update(hall);
        return "redirect:/halls";
    }

    @GetMapping("/addHall")
    public String addHall(Model model, HttpSession session) {
        model.addAttribute("hall", new Hall(0, "New hall", 1, 1));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "addHall";
    }

    @PostMapping("/createHall")
    public String createHall(@ModelAttribute Hall hall,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            hallService.add(hall);
        } catch (Exception e) {
            redirectAttributes.addAttribute("errorMessage", String.format(
                    "Error occurred while creating new hall. %s.",
                    e.getMessage()));
            return "redirect:/errorWhenCreateNewHall";
        }
        return "redirect:/halls";
    }

    @GetMapping("/errorWhenCreateNewHall")
    public String errorWhenCreateNewHall(Model model,
                                         HttpSession session,
                                         @RequestParam(name = "errorMessage",
                                                 defaultValue = "") String errorMessage) {
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "errorWhenCreateNewHall";
    }

    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model,
                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("errorMessage", e);
        if (e.getMessage().contains("Failed to convert property value of type 'java.lang.String'"
                + " to required type 'int' for property 'numOfRows'")) {
            redirectAttributes.addAttribute("errorMessage", "Invalid value in the field"
                    + " <Numbers of rows.> Can't create new item 'Hall' with this value.");
        } else if (e.getMessage().contains("Failed to convert property value of type "
                + "'java.lang.String' to required type 'int' for property 'numOfSeats'")) {
            redirectAttributes.addAttribute("errorMessage", "Invalid value in the field"
                    + " <Numbers of seats.> Can't create new item 'Hall' with this value.");
        } else {
            LoggerService.LOGGER.error("Exception HallController.java", e);
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/errorWhenCreateNewHall";
    }
}
