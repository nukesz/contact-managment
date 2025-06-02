package com.nukesz.github.contact;

import java.util.HashMap;
import java.util.Map;

public final class Contact {
    private Long id;
    private String first;
    private String last;
    private String phone;
    private String email;
    private Map<String, String> errors = new HashMap<>();

    public Contact() {}

    public Contact(
            Long id,
            String first,
            String last,
            String phone,
            String email,
            Map<String, String> errors
    ) {
        this.id = id;
        this.first = first;
        this.last = last;
        this.phone = phone;
        this.email = email;
        this.errors = errors;
    }

    public Contact copy() {
        return new Contact(id, first, last, phone, email, new HashMap<>(errors));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
