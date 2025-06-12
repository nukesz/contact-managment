package com.nukesz.github.contact;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;

@Controller
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contacts")
    public String getContacts(@RequestParam(value = "q", required = false) String search,
                              @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                              @RequestHeader(value = "HX-Trigger", required = false) String hxTrigger,
                              Model model) {
        List<Contact> contacts;
        if (search != null) {
            contacts = contactService.search(search);
            if ("search".equals(hxTrigger)) {
                model.addAttribute("contacts", contacts);
                return "fragments/contacts :: rows";
            }
        } else {
            contacts = contactService.getAll(page);
        }

        model.addAttribute("contacts", contacts);
        model.addAttribute("page", page);
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

    @GetMapping("/contacts/{contactId}/email")
    @ResponseBody
    public String getContactEmail(@PathVariable("contactId") Long contactId, @RequestParam(name = "email") String email) {
        Contact contact = contactService.find(contactId);
        contact.setEmail(email);
        contactService.validate(contact);
        return contact.getErrors().getOrDefault("email", "");
    }

    @GetMapping("/contacts/count")
    @ResponseBody
    public String getContactsCount() {
        var count = contactService.count();
        return "(" + count + " total Counts)";
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

    @DeleteMapping("/contacts")
    public String deleteContacts(@RequestParam(name = "selected_contact_ids") List<Long> contactIds,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        for (Long contactId : contactIds) {
            Contact contact = contactService.find(contactId);
            contactService.delete(contact);
        }
        redirectAttributes.addFlashAttribute("message", "Deleted Contacts!");
        List<Contact> contacts = contactService.getAll(1);
        model.addAttribute("contacts", contacts);
        return "index";
    }

    @DeleteMapping("/contacts/{contactId}")
    public ResponseEntity<?> deleteContact(@PathVariable("contactId") Long contactId, HttpServletRequest request,
                                           @RequestHeader(value = "HX-Trigger", required = false) String hxTrigger,
                                           RedirectAttributes redirectAttributes) {
        Contact contact = contactService.find(contactId);
        contactService.delete(contact);
        if ("delete-btn".equals(hxTrigger)) {
            redirectAttributes.addFlashAttribute("message", "Deleted Contact!");
            URI redirectUri = URI.create("/contacts");
            return ResponseEntity.status(HttpStatus.SEE_OTHER).location(redirectUri).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }
}