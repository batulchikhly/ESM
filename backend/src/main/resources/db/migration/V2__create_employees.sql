CREATE TABLE employees (
    id UUID PRIMARY KEY,
    employee_code VARCHAR(32) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(320) NOT NULL,
    country CHAR(2) NOT NULL,
    department VARCHAR(100) NOT NULL,
    job_title VARCHAR(150) NOT NULL,
    employment_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_employees_employee_code UNIQUE (employee_code),
    CONSTRAINT uq_employees_email UNIQUE (email),
    CONSTRAINT ck_employees_employment_status CHECK (employment_status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_employees_country ON employees (country);
CREATE INDEX idx_employees_department ON employees (department);
CREATE INDEX idx_employees_employment_status ON employees (employment_status);
CREATE INDEX idx_employees_last_first_name ON employees (lower(last_name), lower(first_name));
