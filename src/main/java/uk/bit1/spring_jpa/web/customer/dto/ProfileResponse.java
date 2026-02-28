package uk.bit1.spring_jpa.web.customer.dto;

public record ProfileResponse(
        Long id,
        String emailAddress,
        boolean marketingOptIn
) {}