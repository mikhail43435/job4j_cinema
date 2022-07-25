package ru.job4j.cinema.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.exceptions.DuplicateCustomerEmailException;
import ru.job4j.cinema.exceptions.DuplicateCustomerPhoneException;
import ru.job4j.cinema.model.Customer;
import ru.job4j.cinema.service.CustomerService;
import ru.job4j.cinema.service.TicketService;
import ru.job4j.cinema.util.CustomerHandler;
import ru.job4j.cinema.util.MenuHTMLGenerator;
import ru.job4j.cinema.util.SessionTypeHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static ru.job4j.cinema.util.CustomerHandler.getCustomerOfCurrentSession;
import static ru.job4j.cinema.util.UserHandler.getUserOfCurrentSession;

@ThreadSafe
@Controller
public class CustomerController {

    private static final String HTML_MENU_CODE_TO_INJECT_STRING = "htmlMenuCodeToInject";
    private final CustomerService customerService;
    private final TicketService ticketService;

    public CustomerController(CustomerService customerService, TicketService ticketService) {
        this.customerService = customerService;
        this.ticketService = ticketService;
    }

    @GetMapping("/customers")
    public String customers(Model model, HttpSession session) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "customers"));
        return "customers";
    }

    @GetMapping("/customerTickets")
    public String customerTickets(Model model, HttpSession session) {
        model.addAttribute("tickets",
                ticketService.findByCustomerId(getCustomerOfCurrentSession(session).getId()));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "customerTickets"));
        model.addAttribute("sessionType", SessionTypeHandler.getCurrentSessionType(session));
        return "customerTickets";
    }

    @GetMapping("/loginCustomerPage")
    public String loginCustomerPage(Model model,
                                    @RequestParam(name = "fail", required = false) Boolean fail,
                                    HttpSession session) {
        model.addAttribute("fail", fail != null);
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "loginAsCustomer"));
        return "loginCustomerPage";
    }

    @PostMapping("/loginCustomer")
    public String loginCustomer(@ModelAttribute Customer customer, HttpServletRequest req) {
        Optional<Customer> customerDb =
                customerService.findByEmailAndPassword(customer.getEmail(), customer.getPassword());
        if (customerDb.isEmpty()) {
            return "redirect:/loginCustomerPage?fail=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("customer", customerDb.get());
        session.setAttribute("sessionType", "customer");
        return "redirect:/sessions";
    }

    @GetMapping("/customerRegistrationPage")
    public String customerRegistrationPage(Model model,
                                           @RequestParam(name = "fail",
                                                   required = false) Boolean fail,
                                           @RequestParam(name = "errorMessage",
                                                   defaultValue = "") String errorMessage,
                                           HttpSession session) {
        model.addAttribute("fail", fail != null);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "customerRegistrationPage";
    }

    @PostMapping("/registrationCustomer")
    public String registrationCustomer(Model model,
                                       @ModelAttribute Customer customer,
                                       HttpServletRequest req,
                                       RedirectAttributes redirectAttributes) {
        Customer regCustomer;
        String redirectString = "redirect:/customerRegistrationPage?fail=true";
        try {
            regCustomer = customerService.add(customer);
        } catch (DuplicateCustomerEmailException e) {
            redirectAttributes.addAttribute("errorMessage", String.format(
                    "Error occurred while registration new customer."
                            + "Customer with e-mail <%s> already exists.",
                    customer.getEmail()));
            return redirectString;
        } catch (DuplicateCustomerPhoneException e) {
            redirectAttributes.addAttribute("errorMessage", String.format(
                    "Error occurred while registration new customer."
                            + "Customer with phone <%s> already exists.",
                    customer.getPhone()));
            return redirectString;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("errorMessage", String.format(
                    "Error occurred while registration new customer. %s", e.getMessage()));
            return redirectString;
        } catch (Exception e) {
            redirectAttributes.addAttribute("errorMessage", String.format(
                    "Error occurred while registration new customer with e-mail <%s>.",
                    customer.getEmail()
            ));
            return redirectString;
        }
        HttpSession session = req.getSession();
        session.setAttribute("customer", regCustomer);
        session.setAttribute("sessionType", "customer");
        return "redirect:/successCustomerRegistration";
    }

    @GetMapping("/successCustomerRegistration")
    public String successCustomerRegistration(Model model,
                                              HttpServletRequest req,
                                              HttpSession session) {
        model.addAttribute("customer", CustomerHandler.getCustomerOfCurrentSession(session));
        model.addAttribute("user", getUserOfCurrentSession(session));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, ""));
        return "successCustomerRegistration";
    }

    @GetMapping("/logoutCustomer")
    public String logoutCustomer(HttpSession session) {
        session.invalidate();
        return "redirect:/sessions";
    }

    @GetMapping("/customerInfo")
    public String customerInfo(Model model,
                               HttpSession session) {
        model.addAttribute("customer", getCustomerOfCurrentSession(session));
        model.addAttribute(HTML_MENU_CODE_TO_INJECT_STRING,
                MenuHTMLGenerator.generate(session, "customerInfo"));
        return "customerInfo";
    }
}