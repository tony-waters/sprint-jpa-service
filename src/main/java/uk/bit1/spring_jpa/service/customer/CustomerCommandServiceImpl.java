package uk.bit1.spring_jpa.service.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.bit1.spring_jpa.entity.Customer;
import uk.bit1.spring_jpa.entity.Profile;
import uk.bit1.spring_jpa.repository.CustomerRepository;
import uk.bit1.spring_jpa.repository.ProfileRepository;
import uk.bit1.spring_jpa.service.NotFoundException;
import uk.bit1.spring_jpa.web.customer.dto.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final ProfileRepository profileRepository;
    private final CustomerViewMapper mapper;

    @Override
    public CustomerResponse create(CustomerCreateRequest req) {
        Customer c = new Customer(req.displayName());
        c = customerRepository.save(c);
        return mapper.toResponse(c);
    }

    @Override
    public CustomerResponse changeDisplayName(Long customerId, CustomerUpdateDisplayNameRequest req) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        c.changeDisplayName(req.displayName());
        return mapper.toResponse(c);
    }

    @Override
    public CustomerResponse createProfile(Long customerId, ProfileCreateRequest req) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        c.createProfile(req.emailAddress(), req.marketingOptIn());
        return mapper.toResponse(c);
    }

    @Override
    public CustomerResponse attachProfile(Long customerId, ProfileAttachRequest req) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));

        Profile p = profileRepository.findById(req.profileId())
                .orElseThrow(() -> new NotFoundException("Profile " + req.profileId() + " not found"));

        c.attachProfile(p);
        return mapper.toResponse(c);
    }

    @Override
    public void removeProfile(Long customerId) {
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer " + customerId + " not found"));
        c.removeProfile();
    }
}