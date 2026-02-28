package uk.bit1.spring_jpa.service.customer;

import org.springframework.stereotype.Component;
import uk.bit1.spring_jpa.entity.Customer;
import uk.bit1.spring_jpa.entity.Profile;
import uk.bit1.spring_jpa.web.customer.dto.CustomerResponse;
import uk.bit1.spring_jpa.web.customer.dto.ProfileResponse;

@Component
public class CustomerViewMapper {

    public CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getDisplayName(),
                mapProfile(c.getProfile())
        );
    }

    private ProfileResponse mapProfile(Profile p) {
        if (p == null) return null;
        return new ProfileResponse(
                p.getId(),
                p.getEmailAddress(),
                p.isMarketingOptIn() // adjust if your getter differs
        );
    }
}