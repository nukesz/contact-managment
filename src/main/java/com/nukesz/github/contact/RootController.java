package com.nukesz.github.contact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToContacts() {
        return "redirect:/contacts";
    }
}
