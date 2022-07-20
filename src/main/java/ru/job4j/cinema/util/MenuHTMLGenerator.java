package ru.job4j.cinema.util;

import javax.servlet.http.HttpSession;

public class MenuHTMLGenerator {

    private MenuHTMLGenerator() {
    }

    public static String generate(HttpSession session, String currPage) {
        String sessionType = SessionTypeHandler.getCurrentSessionType(session);
        String stringSeparator = System.lineSeparator() + "            ";
        StringBuilder sb = new StringBuilder();

        String halls = "<a class=\"nav-item nav-link\" href=\"/halls\">Halls</a>";
        String hallsCurr = "<a class=\"nav-item nav-link active\" href=\"#\">Halls "
                + "<span class=\"sr-only\">(current)</span></a>";

        String sessions = "<a class=\"nav-item nav-link\" href=\"/sessions\">Film sessions</a>";
        String sessionsCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "Film sessions <span class=\"sr-only\">(current)</span></a>";
        String sessionsString = currPage.equals("sessions") ? sessionsCurr : sessions;

        String tickets = "<a class=\"nav-item nav-link\" href=\"/tickets\">Tickets</a>";
        String ticketsCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "Tickets <span class=\"sr-only\">(current)</span></a>";

        String customers = "<a class=\"nav-item nav-link\" href=\"/customers\">Customers</a>";
        String customersCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "Customers <span class=\"sr-only\">(current)</span></a>";

        String customerTickets = "<a class=\"nav-item nav-link\" href=\"/customerTickets\">"
                + "My tickets</a>";
        String customerTicketsCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "My tickets <span class=\"sr-only\">(current)</span></a>";

        String loginAsUser = "<a class=\"nav-item nav-link\" href=\"/loginUserPage\">"
                + "Login as user</a>";
        String loginAsUserCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "Login as user <span class=\"sr-only\">(current)</span></a>";

        String loginAsCustomer = "<a class=\"nav-item nav-link\" href=\"/loginCustomerPage\">"
                + "Login as customer</a>";
        String loginAsCustomerCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "Login as customer <span class=\"sr-only\">(current)</span></a>";

        String customerInfo = "<a class=\"nav-item nav-link\" href=\"/customerInfo\">My info</a>";
        String customerInfoCurr = "<a class=\"nav-item nav-link active\" href=\"#\">"
                + "My info <span class=\"sr-only\">(current)</span></a>";

        String logoutCustomer = String.format("<a class=\"nav-item nav-link\" "
                + "href=\"/logoutCustomer\">"
                + "Logout customer (%s)</a>",
                CustomerHandler.getCustomerOfCurrentSession(session).getName());
        String logoutUser = String.format("<a class=\"nav-item nav-link\" href=\"/logoutUser\">"
                + "Logout user (%s)</a>", UserHandler.getUserOfCurrentSession(session).getEmail());

        if (sessionType.equals("user")) {
            sb.append(currPage.equals("halls") ? hallsCurr : halls).
                    append(stringSeparator).
                    append(sessionsString).
                    append(stringSeparator).
                    append(currPage.equals("tickets") ? ticketsCurr : tickets).
                    append(stringSeparator).
                    append(currPage.equals("customers") ? customersCurr : customers).
                    append(stringSeparator).
                    append(logoutUser);
        } else if (sessionType.equals("customer")) {
            sb.append(sessionsString).
                    append(stringSeparator).
                    append(currPage.
                            equals("customerTickets") ? customerTicketsCurr : customerTickets).
                    append(stringSeparator).
                    append(currPage.equals("customerInfo") ? customerInfoCurr : customerInfo).
                    append(stringSeparator).
                    append(logoutCustomer);
        } else {
            sb.append(sessionsString).
                    append(stringSeparator).
                    append(currPage.equals("loginAsUser") ? loginAsUserCurr : loginAsUser).
                    append(stringSeparator).
                    append(currPage.
                            equals("loginAsCustomer") ? loginAsCustomerCurr : loginAsCustomer);
        }
        return sb.toString();
    }
}