package uk.bit1.spring_jpa.service.customer;

import uk.bit1.spring_jpa.web.customer.dto.*;

public interface CustomerCommandService {
    CustomerResponse create(CustomerCreateRequest req);
    CustomerResponse changeDisplayName(Long customerId, CustomerUpdateDisplayNameRequest req);

    CustomerResponse createProfile(Long customerId, ProfileCreateRequest req);
    CustomerResponse attachProfile(Long customerId, ProfileAttachRequest req);
    void removeProfile(Long customerId);
}
