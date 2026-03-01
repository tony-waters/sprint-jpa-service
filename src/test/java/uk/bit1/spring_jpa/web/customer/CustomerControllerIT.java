package uk.bit1.spring_jpa.web.customer;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import uk.bit1.spring_jpa.entity.Customer;
import uk.bit1.spring_jpa.entity.Profile;
import uk.bit1.spring_jpa.repository.CustomerRepository;
import uk.bit1.spring_jpa.repository.ProfileRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerIT {

    @Autowired MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired CustomerRepository customerRepository;
    @Autowired ProfileRepository profileRepository;

    @BeforeEach
    void cleanDb() {
        // Order matters if FKs exist. Safe baseline:
        customerRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    void createCustomer_thenGetById() throws Exception {
        // POST /api/customers
        String body = """
                { "displayName": "Alice" }
                """;

        String json = mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.displayName").value("Alice"))
                .andExpect(jsonPath("$.profile").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(json).get("id").asLong();
        assertThat(id).isPositive();

        // GET /api/customers/{id}
        mvc.perform(get("/api/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) id)) // jsonPath treats small longs as int
                .andExpect(jsonPath("$.displayName").value("Alice"))
                .andExpect(jsonPath("$.profile").doesNotExist());
    }

    @Test
    void patchDisplayName_updatesCustomer() throws Exception {
        Customer c = customerRepository.saveAndFlush(new Customer("Alice"));

        mvc.perform(patch("/api/customers/{id}/display-name", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"displayName\": \"Alice Updated\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId().intValue()))
                .andExpect(jsonPath("$.displayName").value("Alice Updated"));

        Customer reloaded = customerRepository.findById(c.getId()).orElseThrow();
        assertThat(reloaded.getDisplayName()).isEqualTo("Alice Updated");
    }

    @Test
    void createProfile_setsProfileOnCustomer() throws Exception {
        Customer c = customerRepository.saveAndFlush(new Customer("Alice"));

        mvc.perform(post("/api/customers/{id}/profile", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "emailAddress": "alice@example.com",
                                  "marketingOptIn": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId().intValue()))
                .andExpect(jsonPath("$.profile.id").isNumber())
                .andExpect(jsonPath("$.profile.emailAddress").value("alice@example.com"))
                .andExpect(jsonPath("$.profile.marketingOptIn").value(true));

        Customer reloaded = customerRepository.findWithProfileById(c.getId()).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().getEmailAddress()).isEqualTo("alice@example.com");
        assertThat(reloaded.getProfile().isMarketingOptIn()).isTrue();
    }

    @Test
    void removeProfile_deletesRelationship() throws Exception {
        Customer c = customerRepository.saveAndFlush(new Customer("Alice"));
        c.createProfile("alice@example.com", false);
        customerRepository.saveAndFlush(c);

        mvc.perform(delete("/api/customers/{id}/profile", c.getId()))
                .andExpect(status().isNoContent());

        Customer reloaded = customerRepository.findById(c.getId()).orElseThrow();
        assertThat(reloaded.getProfile()).isNull();
    }

    @Test
    void attachExistingProfile_linksProfileToCustomer() throws Exception {
        // Profile exists independently (your model allows this, since Profile has its own @Id)
        Profile p = profileRepository.saveAndFlush(new Profile("shared@example.com", false));
        Customer c = customerRepository.saveAndFlush(new Customer("Alice"));

        mvc.perform(put("/api/customers/{id}/profile", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "profileId": %d }
                                """.formatted(p.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(c.getId().intValue()))
                .andExpect(jsonPath("$.profile.id").value(p.getId().intValue()))
                .andExpect(jsonPath("$.profile.emailAddress").value("shared@example.com"));

        Customer reloaded = customerRepository.findById(c.getId()).orElseThrow();
        assertThat(reloaded.getProfile()).isNotNull();
        assertThat(reloaded.getProfile().getId()).isEqualTo(p.getId());
    }

    @Test
    void attachProfile_twice_shouldFailWithConflict() throws Exception {
        Profile p1 = profileRepository.saveAndFlush(new Profile("p1@example.com", false));
        Profile p2 = profileRepository.saveAndFlush(new Profile("p2@example.com", false));
        Customer c = customerRepository.saveAndFlush(new Customer("Alice"));

        // attach first
        mvc.perform(put("/api/customers/{id}/profile", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "profileId": %d }
                                """.formatted(p1.getId())))
                .andExpect(status().isOk());

        // attach second -> domain throws IllegalStateException => 409
        mvc.perform(put("/api/customers/{id}/profile", c.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "profileId": %d }
                                """.formatted(p2.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("CONFLICT"));
    }

    @Test
    void attachSameProfileToTwoCustomers_shouldConflict_dueToUniqueFk() throws Exception {
        // This depends on your DB constraint: Customer.profile_id unique = true
        Profile shared = profileRepository.saveAndFlush(new Profile("shared@example.com", false));

        Customer a = customerRepository.saveAndFlush(new Customer("Alice"));
        Customer b = customerRepository.saveAndFlush(new Customer("Bob"));

        // Attach to A
        mvc.perform(put("/api/customers/{id}/profile", a.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    { "profileId": %d }
                                """.formatted(shared.getId())))
                .andExpect(status().isOk());

        // Attach same profile to B -> should hit DB constraint => DataIntegrityViolation => mapped to 409
        mvc.perform(put("/api/customers/{id}/profile", b.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "profileId": %d }
                                """.formatted(shared.getId())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DATA_INTEGRITY_VIOLATION"));
    }

    @Test
    void createCustomer_validationError_blankDisplayName() throws Exception {
        mvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "displayName": "   " }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.displayName").exists());
    }

    @Test
    void getMissingCustomer_returns404() throws Exception {
        mvc.perform(get("/api/customers/{id}", 999999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }
}