package uk.bit1.spring_jpa.web.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.bit1.spring_jpa.service.customer.CustomerCommandService;
import uk.bit1.spring_jpa.service.customer.CustomerQueryService;
import uk.bit1.spring_jpa.web.customer.dto.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerCommandService commandService;
    private final CustomerQueryService queryService;

    // ---- Queries ----

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable Long id) {
        return queryService.getById(id);
    }

    @GetMapping
    public List<CustomerResponse> getAll() {
        return queryService.getAll();
    }

    // ---- Commands ----

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@RequestBody @Valid CustomerCreateRequest req) {
        return commandService.create(req);
    }

    @PatchMapping("/{id}/display-name")
    public CustomerResponse changeDisplayName(
            @PathVariable Long id,
            @RequestBody @Valid CustomerUpdateDisplayNameRequest req
    ) {
        return commandService.changeDisplayName(id, req);
    }

    @PostMapping("/{id}/profile")
    public CustomerResponse createProfile(
            @PathVariable Long id,
            @RequestBody @Valid ProfileCreateRequest req
    ) {
        return commandService.createProfile(id, req);
    }

    @PutMapping("/{id}/profile")
    public CustomerResponse attachProfile(
            @PathVariable Long id,
            @RequestBody @Valid ProfileAttachRequest req
    ) {
        return commandService.attachProfile(id, req);
    }

    @DeleteMapping("/{id}/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProfile(@PathVariable Long id) {
        commandService.removeProfile(id);
    }
}