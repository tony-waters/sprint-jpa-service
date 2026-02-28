package uk.bit1.spring_jpa.web.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileCreateRequest(
        @NotBlank
        @Email
        @Size(min = 2, max = 50)
        String emailAddress,
        boolean marketingOptIn
) {}