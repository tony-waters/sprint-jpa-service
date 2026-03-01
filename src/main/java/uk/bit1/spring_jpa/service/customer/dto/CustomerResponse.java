package uk.bit1.spring_jpa.service.customer.dto;

public record CustomerResponse(
        Long id,
        String displayName,
        ProfileResponse profile
) {}