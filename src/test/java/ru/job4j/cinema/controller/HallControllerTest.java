package ru.job4j.cinema.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.service.HallService;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class HallControllerTest {

    private final Model model = mock(Model.class);
    private final HttpSession httpSession = mock(HttpSession.class);
    private final HallService hallService = mock(HallService.class);

    @Test
    void whenFindAll() {
        List<Hall> list = List.of(new Hall(0, "hall 1", 10, 20));
        when(hallService.findAll()).thenReturn(list);
        HallController hallController = new HallController(hallService);
        String result = hallController.halls(model, httpSession);

        String htmlMenuCodeToInject = "<a class=\"nav-item nav-link\" "
                + "href=\"/sessions\">Film sessions</a>"
                + System.lineSeparator()
                + "            <a class=\"nav-item nav-link\" "
                + "href=\"/loginUserPage\">Login as user</a>"
                + System.lineSeparator()
                + "            <a class=\"nav-item nav-link\" "
                + "href=\"/loginCustomerPage\">Login as customer</a>";

        verify(model).addAttribute("halls", list);
        verify(model).addAttribute("htmlMenuCodeToInject", htmlMenuCodeToInject);
        assertThat(result).isEqualTo("halls");
    }

}