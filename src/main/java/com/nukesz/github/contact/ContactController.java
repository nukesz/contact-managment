package com.nukesz.github.contact;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    public String getContacts(@RequestParam(value = "q", required = false) String search, Model model) {
        List<Contact> contacts;
        if (search != null && !search.isEmpty()) {
            contacts = contactService.search(search);  // <2> Search
        } else {
            contacts = contactService.getAll();        // <3> Get all
        }

        model.addAttribute("contacts", contacts);
        return "index";  // Thymeleaf template: src/main/resources/templates/index.html
    }
}