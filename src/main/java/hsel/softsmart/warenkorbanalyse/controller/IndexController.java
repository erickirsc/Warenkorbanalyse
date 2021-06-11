package hsel.softsmart.warenkorbanalyse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Standardwebschnittstelle, die allgemeine Pfade behandelt.
 */
@Controller
public class IndexController {

    /**
     * Wenn kein Pfad angegeben wird, so wird dieser an /analysis weitergeleitet.
     *
     * @return Client wird an /analysis weitergeleitet
     */
    @GetMapping
    public String index() {
        return "redirect:/analysis";
    }

    /**
     * Sendet dem Client die Login View.
     *
     * @return Client wird an /login weitergeleitet
     */
    @GetMapping("login")
    public String login() {
        return "login";
    }
}
