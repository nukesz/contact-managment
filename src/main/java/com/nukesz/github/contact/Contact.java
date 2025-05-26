package com.nukesz.github.contact;

import java.util.Map;

public record Contact(
        int id,
        String first,
        String last,
        String phone,
        String email,
        Map<String, String> errors
) {}
