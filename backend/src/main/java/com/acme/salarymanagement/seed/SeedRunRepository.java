package com.acme.salarymanagement.seed;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRunRepository extends JpaRepository<SeedRun, UUID> {

    Optional<SeedRun> findBySeedNameAndSeedVersion(String seedName, String seedVersion);
}
