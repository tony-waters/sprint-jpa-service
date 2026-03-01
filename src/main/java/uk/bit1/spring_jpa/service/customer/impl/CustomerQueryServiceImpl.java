package uk.bit1.spring_jpa.service.customer.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.bit1.spring_jpa.entity.Customer;
import uk.bit1.spring_jpa.repository.CustomerRepository;
import uk.bit1.spring_jpa.service.NotFoundException;
import uk.bit1.spring_jpa.service.customer.CustomerQueryService;
import uk.bit1.spring_jpa.service.customer.dto.CustomerResponse;
import uk.bit1.spring_jpa.service.customer.mapper.CustomerViewMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerRepository customerRepository;
    private final CustomerViewMapper mapper;

    @Override
    public CustomerResponse getById(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        return mapper.toResponse(c);
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}