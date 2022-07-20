package ru.job4j.cinema.controller;

import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserService;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.cinema.util.MenuHTMLGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@ThreadSafe
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/loginUserPage")
    public String loginPage(Model model,
                            @RequestParam(name = "fail", required = false) Boolean fail,
                            HttpSession session) {
        model.addAttribute("fail", fail != null);
        model.addAttribute("htmlMenuCodeToInject",
                MenuHTMLGenerator.generate(session, "loginAsUser"));
        return "loginUserPage";
    }

    @PostMapping("/loginUser")
    public String login(@ModelAttribute User user, HttpServletRequest req) {
        Optional<User> userDb = userService.findByEmailAndPassword(
                user.getEmail(), user.getPassword()
        );
        if (userDb.isEmpty()) {
            return "redirect:/loginUserPage?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", userDb.get());
        session.setAttribute("sessionType", "user");
        return "redirect:/sessions";
    }

    @GetMapping("/logoutUser")
    public String logoutUser(HttpSession session) {
        session.invalidate();
        return "redirect:/sessions";
    }
}