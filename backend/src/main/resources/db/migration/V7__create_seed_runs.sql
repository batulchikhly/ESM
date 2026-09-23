CREATE TABLE seed_runs (
    id UUID PRIMARY KEY,
    seed_name VARCHAR(100) NOT NULL,
    seed_version VARCHAR(50) NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_seed_runs_name_version UNIQUE (seed_name, seed_version)
);
