package uk.bit1.spring_jpa.service.customer.dto;

import jakarta.validation.constraints.NotNull;

public record ProfileAttachRequest(
        @NotNull Long profileId
) {}