package com.nukesz.github.contact;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private List<Contact> contacts;

    @PostConstruct
    public void init() {
        loadDb();
    }

    private void loadDb() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/contacts.json");
            contacts = objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load contacts.json", e);
        }
    }

    public void saveDb() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File("src/main/resources/contacts.json"), contacts);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save contacts.json", e);
        }
    }

    public List<Contact> getAll(int page) {
        int pageStart = (page - 1) * 10;
        int pageEnd = pageStart + 10;
        if (pageStart >= contacts.size()) {
            return Collections.emptyList();
        }
        if (pageEnd > contacts.size()) {
            return contacts.subList(pageStart, contacts.size() - 1);
        }
        return contacts.subList(pageStart, pageEnd);
    }

    public List<Contact> search(String query) {
        String lowerQuery = query.toLowerCase();
        return contacts.stream()
                .filter(c ->
                        safeContains(c.getFirst(), lowerQuery) ||
                                safeContains(c.getLast(), lowerQuery) ||
                                safeContains(c.getEmail(), lowerQuery) ||
                                safeContains(c.getPhone(), lowerQuery))
                .collect(Collectors.toList());
    }

    public Contact find(Long contactId) {
        return contacts.stream().filter(contact -> contact.getId().equals(contactId)).findFirst().map(Contact::copy).orElseThrow();
    }

    private boolean safeContains(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }

    public boolean save(Contact contact) {
        contact.getErrors().clear();
        if (!validate(contact)) {
            return false;
        }

        long maxId;
        if (contact.getId() == null) {
            if (contacts.isEmpty()) {
                maxId = 1L;
            } else {
                maxId = contacts.stream().map(Contact::getId).max(Long::compareTo).orElse(1L);
            }
            contact.setId(maxId + 1);
            contacts.add(contact);
        }
        saveDb();
        return true;
    }


    public void delete(Contact contact) {
         contacts.removeIf(c -> c.getId().equals(contact.getId()));
         saveDb();
    }

    public boolean validate(Contact contact) {
        if (contact.getEmail() == null || contact.getEmail().isBlank()) {
            contact.getErrors().put("email", "Email Required");
        } else {
            boolean duplicate = contacts.stream()
                    .anyMatch(c -> !Objects.equals(c.getId(), contact.getId()) && contact.getEmail().equals(c.getEmail()));
            if (duplicate) {
                contact.getErrors().put("email", "Email Must Be Unique");
            }
        }
        return contact.getErrors().isEmpty();
    }

    public int count() {
        return contacts.size();
    }
}
