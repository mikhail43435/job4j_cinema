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

    private final HallService service;

    public HallController(HallService hallService) {
        this.service = hallService;
    }

    @GetMapping("/halls")
    public String posts(Model model) {
        model.addAttribute("halls", service.findAll());
        return "halls";
    }

    @GetMapping("/updateHall/{hallId}")
    public String formUpdateHall(Model model, @PathVariable("hallId") int id) {
        model.addAttribute("hall", service.findById(id));
        return "updateHall";
    }

    @PostMapping("/updateHall")
    public String updateHall(@ModelAttribute Hall hall) {
        service.update(hall);
        return "redirect:/halls";
    }

    @GetMapping("/addHall")
    public String addHall(Model model) {
        model.addAttribute("hall", new Hall(0, "New hall", 1, 1));
        return "addHall";
    }

    @PostMapping("/createHall")
    public String createHall(@ModelAttribute Hall hall) {
        service.add(hall);
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