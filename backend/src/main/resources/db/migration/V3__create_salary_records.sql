CREATE TABLE salary_records (
    id UUID PRIMARY KEY,
    employee_id UUID NOT NULL,
    annual_salary NUMERIC(19, 4) NOT NULL,
    currency CHAR(3) NOT NULL,
    effective_from DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID NOT NULL,
    CONSTRAINT fk_salary_records_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_salary_records_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT uq_salary_records_employee_effective_date UNIQUE (employee_id, effective_from),
    CONSTRAINT ck_salary_records_annual_salary CHECK (annual_salary >= 0),
    CONSTRAINT ck_salary_records_currency CHECK (currency = UPPER(currency) AND length(currency) = 3)
);

CREATE INDEX idx_salary_records_employee_effective_from
    ON salary_records (employee_id, effective_from DESC);
CREATE INDEX idx_salary_records_effective_from ON salary_records (effective_from);
CREATE INDEX idx_salary_records_currency_salary ON salary_records (currency, annual_salary);
