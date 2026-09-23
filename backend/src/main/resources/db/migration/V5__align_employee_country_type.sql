ALTER TABLE employees
    ALTER COLUMN country TYPE VARCHAR(2)
    USING country::VARCHAR(2);