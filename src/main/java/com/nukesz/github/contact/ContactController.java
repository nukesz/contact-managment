package com.nukesz.github.contact;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
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

    @GetMapping("/contacts/{contactId}")
    public String viewContact(@PathVariable("contactId") Long contactId, Model model) {
        Contact contact = contactService.find(contactId);
        model.addAttribute("contact", contact);
        return "show";
    }

    @GetMapping("/contacts/{contactId}/edit")
    public String showEditContactForm(@PathVariable("contactId") Long contactId, Model model) {
        Contact contact = contactService.find(contactId);
        model.addAttribute("contact", contact);
        return "edit";
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

    @PostMapping("/contacts/{contactId}/edit")
    public String editContact(@PathVariable("contactId") Long contactId, @ModelAttribute Contact contact, Model model,
                              RedirectAttributes redirectAttributes) {
        Contact oldContact = contactService.find(contactId);
        oldContact.setFirst(contact.getFirst());
        oldContact.setLast(contact.getLast());
        oldContact.setPhone(contact.getPhone());
        oldContact.setEmail(contact.getEmail());
        boolean success = contactService.save(oldContact);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Updated Contact!");
            return "redirect:/contacts/" + contactId;
        } else {
            model.addAttribute("contact", contact);
            return "edit";
        }
    }

    @DeleteMapping("/contacts/{contactId}")
    public String deleteContact(@PathVariable("contactId") Long contactId, HttpServletRequest request,
                                RedirectAttributes redirectAttributes) {
        Contact contact = contactService.find(contactId);
        contactService.delete(contact);
        redirectAttributes.addFlashAttribute("message", "Deleted Contact!");
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.SEE_OTHER);
        return "redirect:/contacts";
    }
}