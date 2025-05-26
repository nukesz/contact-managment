package com.nukesz.github.contact;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private List<Contact> contacts;

    @PostConstruct
    public void init() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            InputStream inputStream = getClass().getResourceAsStream("/contacts.json");
            contacts = objectMapper.readValue(inputStream, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load contacts.json", e);
        }
    }

    public List<Contact> getAll() {
        return contacts;
    }

    public List<Contact> search(String query) {
        String lowerQuery = query.toLowerCase();
        return contacts.stream()
                .filter(c ->
                        safeContains(c.first(), lowerQuery) ||
                                safeContains(c.last(), lowerQuery) ||
                                safeContains(c.email(), lowerQuery) ||
                                safeContains(c.phone(), lowerQuery))
                .collect(Collectors.toList());
    }

    private boolean safeContains(String field, String query) {
        return field != null && field.toLowerCase().contains(query);
    }
}
