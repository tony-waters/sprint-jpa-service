package uk.bit1.spring_jpa.web.customer.dto;

public record CustomerResponse(
        Long id,
        String displayName,
        ProfileResponse profile
) {}