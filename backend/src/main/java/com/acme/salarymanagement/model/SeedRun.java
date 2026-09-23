package com.acme.salarymanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "seed_runs", uniqueConstraints = @UniqueConstraint(
        name = "uq_seed_runs_name_version",
        columnNames = {"seed_name", "seed_version"}))
public class SeedRun {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "seed_name", nullable = false, length = 100)
    private String seedName;

    @Column(name = "seed_version", nullable = false, length = 50)
    private String seedVersion;

    @Column(name = "completed_at", nullable = false, updatable = false)
    private Instant completedAt;

    protected SeedRun() {
    }

    public static SeedRun completed(String seedName, String seedVersion, Instant completedAt) {
        SeedRun seedRun = new SeedRun();
        seedRun.seedName = seedName;
        seedRun.seedVersion = seedVersion;
        seedRun.completedAt = completedAt;
        return seedRun;
    }

    public UUID getId() {
        return id;
    }

    public String getSeedName() {
        return seedName;
    }

    public String getSeedVersion() {
        return seedVersion;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
