package online.assistant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    public static boolean active = true;

    @GetMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("state", active);
        return "index";
    }

    @GetMapping(value = "/change-state")
    public String changeState() {
        active = !active;
        return "redirect:/";
    }

}
