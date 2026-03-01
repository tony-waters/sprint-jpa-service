package uk.bit1.spring_jpa.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.bit1.spring_jpa.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @EntityGraph(attributePaths = "profile")
    Optional<Customer> findWithProfileById(Long id);

}
