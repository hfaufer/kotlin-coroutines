package de.hu.java.http.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        Long id,
        String name,
        String username,
        String email,
        Address address,
        String phone,
        String website,
        Company company
) {
}
