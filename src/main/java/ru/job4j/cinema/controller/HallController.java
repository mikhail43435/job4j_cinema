package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.HallService;

@ThreadSafe
@Controller
public class HallController {

    private final HallService hallService;

    public HallController(HallService hallService) {
        this.hallService = hallService;
    }

    @GetMapping("/halls")
    public String posts(Model model) {
        model.addAttribute("halls", hallService.findAll());
        return "halls";
    }

    @GetMapping("/updateHall/{hallId}")
    public String formUpdateHall(Model model, @PathVariable("hallId") int id) {
        model.addAttribute("hall", hallService.findById(id));
        return "updateHall";
    }

    @PostMapping("/updateHall")
    public String updateHall(@ModelAttribute Hall hall) {
        hallService.update(hall);
        return "redirect:/halls";
    }

    @GetMapping("/addHall")
    public String addHall(Model model) {
        model.addAttribute("hall", new Hall(0, "New hall", 1, 1));
        return "addHall";
    }

    @PostMapping("/createHall")
    public String createHall(@ModelAttribute Hall hall) {
        hallService.add(hall);
        return "redirect:/halls";
    }

    @ExceptionHandler({Exception.class})
    public String handleException(Exception e, Model model) {
        return "redirect:/errorWhenCreateNewHall";
    }

    @GetMapping("/errorWhenCreateNewHall")
    public String errorWhenCreateNewHall(Model model) {
        return "/errorWhenCreateNewHall";
    }
}