package uk.bit1.spring_jpa.service.customer.dto;

public record ProfileResponse(
        Long id,
        String emailAddress,
        boolean marketingOptIn
) {}