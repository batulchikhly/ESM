package com.acme.salarymanagement.seed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class SeedRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(SeedRunner.class);
    private final SeedService seedService;

    public SeedRunner(SeedService seedService) {
        this.seedService = seedService;
    }

    @Override
    public void run(ApplicationArguments args) {
        SeedService.SeedResult result = seedService.seed();
        if (result.seeded()) {
            logger.info("Seeded {} employees and {} salary records", result.employeeCount(), result.salaryRecordCount());
        } else {
            logger.info("Synthetic employee seed already completed; no rows inserted");
        }
    }
}
