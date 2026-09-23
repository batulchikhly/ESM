package com.acme.salarymanagement.repository;

import com.acme.salarymanagement.model.SeedRun;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeedRunRepository extends JpaRepository<SeedRun, UUID> {

    Optional<SeedRun> findBySeedNameAndSeedVersion(String seedName, String seedVersion);
}
