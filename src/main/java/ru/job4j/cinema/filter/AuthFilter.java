package ru.job4j.cinema.filter;

import org.springframework.stereotype.Component;
import ru.job4j.cinema.util.SessionTypeHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        String sessionType = SessionTypeHandler.getCurrentSessionType(request.getSession());
        if (uri.endsWith("sessions")
                || uri.endsWith("customerRegistrationPage")
                || uri.endsWith("registrationCustomer")
                || uri.endsWith("errorWhenCreateNewCustomer")
                || uri.endsWith("loginCustomerPage")
                || uri.endsWith("loginUserPage")
                || uri.endsWith("loginUser")
                || uri.endsWith("loginCustomer")) {
            chain.doFilter(request, response);
            return;
        }
        if (sessionType.equals("customer")
                && (uri.endsWith("customerTickets")
                || uri.endsWith("sessionSelection")
                || uri.endsWith("errorWhenSelectSessions")
                || uri.endsWith("addTicket")
                || uri.endsWith("createTicket")
                || uri.endsWith("customerInfo")
                || uri.endsWith("successCustomerRegistration")
                || uri.endsWith("errorWhenCreateNewTicket")
                || uri.endsWith("logoutCustomer"))) {
            chain.doFilter(request, response);
            return;
        }
        if (sessionType.equals("user")
                && (uri.endsWith("halls")
                || uri.endsWith("addHall")
                || uri.contains("updateHall")
                || uri.endsWith("createHall")
                || uri.endsWith("errorWhenCreateNewHall")
                || uri.endsWith("addSession")
                || uri.endsWith("createSession")
                || uri.contains("updateSession")
                || uri.endsWith("customers")
                || uri.endsWith("customerInfo")
                || uri.endsWith("tickets")
                || uri.endsWith("logoutUser"))) {
            chain.doFilter(request, response);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/sessions");
    }
}