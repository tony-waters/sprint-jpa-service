package uk.bit1.spring_jpa.service.customer;

import uk.bit1.spring_jpa.web.customer.dto.CustomerResponse;

import java.util.List;

public interface CustomerQueryService {
    CustomerResponse getById(Long customerId);
    List<CustomerResponse> getAll();
}
