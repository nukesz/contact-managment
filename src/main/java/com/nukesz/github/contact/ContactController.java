package com.nukesz.github.contact;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            contacts = contactService.search(search);
        } else {
            contacts = contactService.getAll();
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("q", search != null ? search : "");
        return "index";
    }

    @GetMapping("/contacts/new")
    public String showNewContactForm(Model model) {
        model.addAttribute("contact", new Contact());
        return "new";
    }

    @PostMapping("/contacts/new")
    public String createContact(@ModelAttribute Contact contact, Model model, RedirectAttributes redirectAttributes) {
        boolean success = contactService.save(contact);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Created New Contact!");
            return "redirect:/contacts";
        } else {
            model.addAttribute("contact", contact);
            return "new";
        }
    }
}