ALTER TABLE salary_records
    ALTER COLUMN currency TYPE VARCHAR(3)
    USING TRIM(currency)::VARCHAR(3);